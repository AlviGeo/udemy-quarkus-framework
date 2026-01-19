package com.cofecode;

import com.cofecode.entity.Games;
import com.cofecode.entity.ResponseModel;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

import javax.print.attribute.standard.Media;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Path("/games")
@Produces(MediaType.APPLICATION_JSON)
public class GreetingResource {

    private final List<Games> gamesList = new ArrayList<>();

    public GreetingResource() {
        gamesList.add(new Games(1L, "R6", "FPS"));
        gamesList.add(new Games(3,"The Elder Scrolls V: Skyrim","RPG"));
        gamesList.add(new Games(2,"Battlefield 1","FPS"));
        gamesList.add(new Games(4,"Cyberpunk 2077","RPG"));
    }

    @GET
    public Response getGameList(
            //@HeaderParam("page") int page,
            //@HeaderParam("size") int size,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("name") String name,
            @CookieParam("gameCategory") String gameCategory
    ) {
        List<Games> pagedGames = gamesList;
        if (name != null && !name.isEmpty()) {
            pagedGames = pagedGames.stream().filter(g -> g.getName().toLowerCase().contains(name.toLowerCase())).collect((Collectors.toList()));
        }

        int totalGames = pagedGames.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, totalGames);

        if (start >= totalGames) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (gameCategory != null && !gameCategory.isEmpty()) {
            pagedGames = pagedGames.stream().sorted((g1, g2) -> {
                boolean isG1InCategory = gameCategory.equalsIgnoreCase(g1.getCategory());
                boolean isG2InCategory = gameCategory.equalsIgnoreCase(g2.getCategory());
                if (isG1InCategory && isG2InCategory) return -1;
                if (isG1InCategory && !isG2InCategory) return 1;
                return 0;
            }).collect(Collectors.toList());
        }

        pagedGames = pagedGames.subList(start, end);
        return Response
                .ok(pagedGames)
                .header("X-Total-Count", totalGames)
                .build();
    }

    @GET
    @Path("/{id}")
    public Response getGame(@PathParam("id") int id) {
        return gamesList.stream()
                .filter(v -> v.getId() == id)
                .findFirst()
                .map(v -> Response.ok(v)
                        .cookie(new NewCookie("gameCategory", v.getCategory(), "/", null, "Games Category", 3600, false))
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createGame(Games game) {
        long id = gamesList.stream().max(Comparator.comparingLong(Games::getId)).get().getId()+1;
        game.setId(id);
        gamesList.add(game);
        return Response.ok(new ResponseModel("Game created", 201)).build();
    }

    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateGame(Games game) {
        gamesList.stream().filter(v -> v.getId() == game.getId()).findFirst().ifPresent(v -> {
            if (game.getName() != null && !game.getName().isEmpty()) {
                v.setName(game.getName());
            }
        });
        return Response.ok(new ResponseModel("Game updated",200)).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response replaceGame(Games game) {
        OptionalInt index = IntStream.range(0, gamesList.size()).filter(i -> gamesList.get(i).getId() == game.getId()).findFirst();
        if (index.isPresent()) {
            gamesList.set(index.getAsInt(), game);
        }
        return Response.ok(new ResponseModel("Game updated",200)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteGame(@PathParam("id") int id) {
        boolean removed = gamesList.removeIf(v -> v.getId() == id);

        if (!removed) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Game not found")
                    .build();
        }

        return Response.ok(new ResponseModel("Game deleted",200)).build();
    }
}