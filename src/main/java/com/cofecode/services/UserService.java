package com.cofecode.services;


import com.cofecode.entity.ResponseModel;
import com.cofecode.entity.User;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import org.mindrot.jbcrypt.BCrypt;

import static jakarta.ws.rs.core.Response.status;

@ApplicationScoped
public class UserService {

    @Transactional
    public void createUser(User user) {
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        user.setId(0);
        User.persist(user);
    }

    public Response login(String username, String password) {
        User user = User.find("username", username).firstResult();

        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ResponseModel("Invalid username or password",401)).build();
        }
        String jwt = Jwt.claims()
                .subject(user.getUsername())
                .groups(user.getRoles())
                .expiresAt(System.currentTimeMillis() / 1000 + 3600)
                .sign();

        return  Response.status(Response.Status.OK).cookie(
                        new NewCookie("JWT",
                                jwt,
                                "/",
                                null,
                                "JWT Token",
                                3600,
                                true,
                                true))
                .entity(new ResponseModel("SUCCESS",200)).build();
    }
}
