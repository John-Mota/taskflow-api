package dev.taskflow.resource;

import dev.taskflow.dto.UserRequest;
import dev.taskflow.dto.UserResponse;
import dev.taskflow.dto.UserUpdateDTO;
import dev.taskflow.service.UserService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService userService;

    @POST
    public Response create(@Valid UserRequest request) {
        UserResponse created = userService.createUser(request);
        return Response.created(URI.create("/api/users/" + created.getId())).entity(created).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        return Response.ok(userService.getUserById(id)).build();
    }

    @GET
    public Response list(
            @QueryParam("page") Integer page,
            @QueryParam("size") Integer size) {
        int pageIndex = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? size : 20;
        
        List<UserResponse> users = userService.listUsers(pageIndex, pageSize);
        return Response.ok(users).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid UserUpdateDTO updateDTO) {
        UserResponse updated = userService.updateUser(id, updateDTO);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        userService.deleteUser(id);
        return Response.noContent().build();
    }
}
