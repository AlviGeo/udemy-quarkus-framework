package com.cofecode;


import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
public class GameResourceTest {

    @Test
    public void testGetUsersEndpoint() {
        RestAssured.given()
                .when().get("/games/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", equalTo("Tomb Rider"))
                .body("category", equalTo("Action"));
    }
}
