package com.team4.project1.domain.item.repository;

import com.team4.project1.domain.item.dto.ItemDto;
import com.team4.project1.domain.item.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableJpaRepositories
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findAllByNameContainingOrderByNameAsc(String keyword, Pageable pageable);
    Page<Item> findAllByNameContainingOrderByPriceAsc(String keyword,Pageable pageable);
}
