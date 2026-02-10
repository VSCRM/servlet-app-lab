package com.example.myapp.resources;

import com.example.myapp.core.User;
import com.example.myapp.db.UserDAO;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    private final UserDAO userDAO;

    public UserResource(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @GET
    @UnitOfWork
    public List<User> getUsers() {
        return userDAO.findAll();
    }

    @GET
    @Path("/{id}")
    @UnitOfWork
    public User getUser(@PathParam("id") LongParam id) {
        return userDAO.findById(id.get())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @POST
    @UnitOfWork
    @Consumes(MediaType.APPLICATION_JSON)
    public User createUser(@Valid User user) {
        return userDAO.create(user);
    }

    @DELETE
    @Path("/{id}")
    @UnitOfWork
    public Response deleteUser(@PathParam("id") LongParam id) {
        userDAO.findById(id.get()).ifPresent(userDAO::delete);
        return Response.noContent().build();
    }
}