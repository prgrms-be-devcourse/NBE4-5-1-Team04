package com.team4.project1.domain.item.repository;

import com.team4.project1.domain.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    @Query("SELECT i FROM Item i ORDER BY i.price ASC")
    List<Item> findAllSortedByPrice();
    @Query("SELECT i FROM Item i ORDER BY i.name ASC")
    List<Item> findAllSortedByName();
}
