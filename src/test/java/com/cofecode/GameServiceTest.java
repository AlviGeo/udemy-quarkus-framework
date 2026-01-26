package com.cofecode;

import com.cofecode.entity.Games;
import com.cofecode.repository.GamesRepository;
import com.cofecode.services.GameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @InjectMocks
    private GameService gameService;

    @Mock
    private GamesRepository gamesRepository;

    @Test
    void testFindPaginatedWithoutName() {
        List<Games> mockGames = List.of(new Games(1L, "Game1", "Category1"));
        Mockito.when(gamesRepository.findPaginated(0, 10, "category"))
                .thenReturn(mockGames);

        List<Games> result = gameService.findPaginated(0, 10, "category", null);

        assertEquals(1, result.size());
        assertEquals("Game1", result.get(0).getName());
        Mockito.verify(gamesRepository).findPaginated(0, 10, "category");
    }
}