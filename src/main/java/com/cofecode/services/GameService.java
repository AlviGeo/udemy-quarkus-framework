package com.cofecode.services;

import com.cofecode.entity.Games;
import com.cofecode.repository.GamesRepository;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Dependent
public class GameService {

    @Inject
    private GamesRepository gamesRepository;

    public List<Games> getAllGames() {
        return gamesRepository.listAll();
    }

    public List<Games> findPaginated(int page, int pageSize, String sortField, String name) {
        if (name != null && !name.isEmpty()) {
            return gamesRepository.findPaginatedByName(page, pageSize, sortField, name);
        }
        return gamesRepository.findPaginated(page, pageSize, sortField);
    }

    public long count(String name) {
        if (name != null && !name.isEmpty()) {
            return gamesRepository.countByName(name);
        }
        return gamesRepository.count();
    }

    public Optional<Games> findById(long id) {
        return gamesRepository.findByIdOptional(id);
    }

    @Transactional
    public void createGames(Games game) {
        game.setId(0);
        gamesRepository.persist(game);
    }

    @Transactional
    public void replaceGame(Games game) {
        gamesRepository.findByIdOptional(game.getId()).ifPresent(v -> gamesRepository.persist(game));
    }

    public void updateGame(long id, String name, String category) {
        gamesRepository.findByIdOptional(id).ifPresent(v -> {
            if (name != null && !name.isEmpty()) {
                v.setName(name);
            }
            if (category != null && !category.isEmpty()) {
                v.setCategory(category);
            }
            gamesRepository.persist(v);
        });
    }

    public void deleteGame(long id) {
        gamesRepository.findByIdOptional(id).ifPresent(v -> {
            gamesRepository.delete(v);
        });
    }
}
