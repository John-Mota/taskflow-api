package dev.taskflow.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.hibernate.exception.ConstraintViolationException;

import java.util.HashMap;
import java.util.Map;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        Map<String, String> responseBody = new HashMap<>();

        if (exception instanceof jakarta.ws.rs.WebApplicationException webAppException) {
            responseBody.put("error", webAppException.getMessage());
            return Response.status(webAppException.getResponse().getStatus())
                    .entity(responseBody)
                    .build();
        }

        Throwable cause = exception.getCause();
        if (cause instanceof ConstraintViolationException constraintViolationException) {
            responseBody.put("error", "Database constraint violation");
            responseBody.put("details", constraintViolationException.getSQLException().getMessage());
            return Response.status(Response.Status.CONFLICT)
                    .entity(responseBody)
                    .build();
        }

        // Generic error response
        responseBody.put("error", "Internal Server Error");
        responseBody.put("details", exception.getMessage());
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(responseBody)
                .build();
    }
}
