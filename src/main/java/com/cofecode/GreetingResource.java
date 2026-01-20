package com.cofecode;

import com.cofecode.entity.Games;
import com.cofecode.entity.ResponseModel;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.print.attribute.standard.Media;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Path("/games")
@Produces(MediaType.APPLICATION_JSON)
@APIResponse(
        responseCode = "200",
        description = "Return operation data.",
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ResponseModel.class)
        )
)
@Tag(name="Game controller")
public class GreetingResource {
    private final List<Games> gamesList = new ArrayList<>();

    public GreetingResource() {
        gamesList.add(new Games(1L, "R6", "FPS"));
        gamesList.add(new Games(3,"The Elder Scrolls V: Skyrim","RPG"));
        gamesList.add(new Games(2,"Battlefield 1","FPS"));
        gamesList.add(new Games(4,"Cyberpunk 2077","RPG"));
    }

    @GET
    @Operation(
            summary= "Get all games",
            description = "Retrieves a paginated list of games. The results can be filtered by game name and sorted by the game"
    )
    @APIResponse(
            responseCode = "200",
            description = "Return games list.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Games.class,type = SchemaType.ARRAY)
            )
    )
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
    @Operation(
            summary = "Get game by id",
            description = "Retrieves specified game by id."
    )
    @APIResponse(
            responseCode = "200",
            description = "Return game.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Games.class)
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "Return operation data.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseModel.class)
            )
    )
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
    @Operation(
            summary = "Create game",
            description = "Create new game."
    )
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createGame(Games game) {
        long id = gamesList.stream().max(Comparator.comparingLong(Games::getId)).get().getId()+1;
        game.setId(id);
        gamesList.add(game);
        return Response.ok(new ResponseModel("Game created", 201)).build();
    }

    @PATCH
    @Operation(
            summary = "Update game",
            description = "Update specified game fields."
    )
    @APIResponse(
            responseCode = "400",
            description = "Return operation data.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseModel.class)
            )
    )
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
    @Operation(
            summary = "Replace game",
            description = "Replace game."
    )
    @APIResponse(
            responseCode = "400",
            description = "Return operation data.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseModel.class)
            )
    )
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
    @Operation(
            summary = "Delete game",
            description = "delete game by id."
    )
    @APIResponse(
            responseCode = "400",
            description = "Return operation data.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseModel.class)
            )
    )
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