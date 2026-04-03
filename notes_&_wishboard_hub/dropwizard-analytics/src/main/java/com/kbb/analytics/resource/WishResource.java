package com.kbb.analytics.resource;

import com.kbb.analytics.db.WishDAO;
import com.kbb.shared.entity.Wish;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Path("/api/wishes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WishResource {

    private final WishDAO dao;

    public WishResource(WishDAO dao) {
        this.dao = dao;
    }

    @GET
    public Response getAll(@QueryParam("userId") Long userId) {
        try {
            List<Wish> list = userId != null ? dao.findByUser(userId) : dao.findAll();
            return Response.ok(list).build();
        } catch (SQLException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/stats")
    public Response getStats() {
        try {
            long total    = dao.countTotal();
            long achieved = dao.countAchieved();
            return Response.ok(Map.of(
                "total",    total,
                "achieved", achieved,
                "pending",  total - achieved
            )).build();
        } catch (SQLException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    public Response create(Wish wish) {
        if (wish.getName() == null || wish.getName().isBlank())
            return Response.status(400).entity("Назва обовязкова").build();
        try {
            return Response.ok(dao.insert(wish)).build();
        } catch (SQLException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}/achieve")
    public Response achieve(@PathParam("id") Long id) {
        try {
            dao.markAchieved(id);
            return Response.ok().build();
        } catch (SQLException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        try {
            dao.delete(id);
            return Response.ok().build();
        } catch (SQLException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
