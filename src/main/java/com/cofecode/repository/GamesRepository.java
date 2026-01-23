package com.cofecode.repository;

import com.cofecode.entity.Games;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class GamesRepository implements PanacheRepositoryBase<Games,Long> {
    public List<Games> findPaginated(int page, int size,String sortBy) {
        if (sortBy != null && !sortBy.isEmpty()) {
            return findAll(Sort.ascending("category"))
                    .page(Page.of(page, size))
                    .list();
        }
        return findAll()
                .page(Page.of(page,size))
                .list();
    }

    public List<Games> findPaginatedByName(int page, int size,String sortBy, String name) {
        if (sortBy != null && !sortBy.isEmpty()) {
            return find("name like ?1",Sort.ascending("category"),"%"+name+"%")
                    .page(Page.of(page, size))
                    .list();
        }
        return find("name like ?1","%"+name+"%")
                .page(Page.of(page,size))
                .list();
    }

    public long countByName(String name){
        return count("name like ?1","%"+name+"%");
    }

}
