package dev.taskflow.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserResourceTest {

    private static Long userId;

    @Test
    @Order(1)
    public void testCreateUser() {
        String userJson = """
                {
                  "username": "testuser",
                  "email": "testuser@example.com",
                  "password": "password123",
                  "role": "ADMIN"
                }
                """;

        userId = given()
                .contentType(ContentType.JSON)
                .body(userJson)
                .when()
                .post("/api/users")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("username", equalTo("testuser"))
                .body("email", equalTo("testuser@example.com"))
                .body("role", equalTo("ADMIN"))
                .extract()
                .path("id");
    }

    @Test
    @Order(2)
    public void testCreateDuplicateUser() {
        String userJson = """
                {
                  "username": "testuser",
                  "email": "testuser2@example.com",
                  "password": "password123",
                  "role": "ADMIN"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(userJson)
                .when()
                .post("/api/users")
                .then()
                .statusCode(409); // Conflict HTTP Status
    }

    @Test
    @Order(3)
    public void testGetUser() {
        given()
                .when()
                .get("/api/users/" + userId)
                .then()
                .statusCode(200)
                .body("username", equalTo("testuser"))
                .body("email", equalTo("testuser@example.com"));
    }

    @Test
    @Order(4)
    public void testUpdateUser() {
        String updateJson = """
                {
                  "username": "updateduser",
                  "role": "USER"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(updateJson)
                .when()
                .put("/api/users/" + userId)
                .then()
                .statusCode(200)
                .body("username", equalTo("updateduser"))
                .body("email", equalTo("testuser@example.com")) // Email should remain
                .body("role", equalTo("USER"));
    }

    @Test
    @Order(5)
    public void testListUsers() {
        given()
                .queryParam("page", 0)
                .queryParam("size", 10)
                .when()
                .get("/api/users")
                .then()
                .statusCode(200)
                .body("size()", org.hamcrest.Matchers.greaterThan(0));
    }

    @Test
    @Order(6)
    public void testDeleteUser() {
        given()
                .when()
                .delete("/api/users/" + userId)
                .then()
                .statusCode(204);

        // Verify deletion
        given()
                .when()
                .get("/api/users/" + userId)
                .then()
                .statusCode(404);
    }
}
