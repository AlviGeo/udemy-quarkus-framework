package com.cofecode;
import com.cofecode.entity.Games;
import com.cofecode.repository.GamesRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


@QuarkusTest
public class GameRepositoryTest {

    @Inject
    GamesRepository repository;

    @Test
    public void testFindById() {
        Games entity = repository.findById(1L);
        assertEquals(entity.getId(),1L);
    }
}
