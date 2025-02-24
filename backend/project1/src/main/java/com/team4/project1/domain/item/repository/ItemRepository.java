package com.team4.project1.domain.item.repository;

import com.team4.project1.domain.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByNameContainingOrderByNameAsc(String keyword);
    List<Item> findAllByNameContainingOrderByPriceAsc(String keyword);
}
