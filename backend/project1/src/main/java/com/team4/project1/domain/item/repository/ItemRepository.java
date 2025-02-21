package com.team4.project1.domain.item.repository;

import com.team4.project1.domain.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOrderByPriceAsc();
    List<Item> findAllByOrderByNameAsc();
    Optional<Item> findByName(String name);
}
