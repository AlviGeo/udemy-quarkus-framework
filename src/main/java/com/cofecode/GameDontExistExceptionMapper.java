package com.cofecode;

import com.cofecode.entity.ResponseModel;
import com.cofecode.exceptions.GameDontExistException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class GameDontExistExceptionMapper implements ExceptionMapper<GameDontExistException> {
    @Override
    public Response toResponse(GameDontExistException exception) {
        return Response.status(Response.Status.NOT_FOUND).entity(new ResponseModel(exception.getMessage(), Response.Status.NOT_FOUND.getStatusCode())).build();
    }
}
