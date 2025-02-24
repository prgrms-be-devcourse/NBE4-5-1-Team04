package com.team4.project1.domain.item.repository;

import com.team4.project1.domain.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * {@link Item} 엔티티에 대한 데이터베이스 작업을 처리하는 리포지토리 인터페이스입니다.
 * {@link JpaRepository}를 확장하여 CRUD 작업을 제공하며,
 * {@link Item} 엔티티와 관련된 데이터 검색 기능을 확장한 메서드를 정의하고 있습니다.
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    /**
     * 주어진 키워드를 포함하는 상품의 목록을 이름 기준으로 오름차순 정렬하고 반환합니다.
     * @param keyword 검색할 키워드
     * @return 키워드를 포함하는 아이템들의 목록을 반환합니다.
     */
    List<Item> findAllByNameContainingOrderByNameAsc(String keyword);

    /**
     * 주어진 키워드를 포함하는 상품의 목록을 가격 기준으로 오름차순 정렬하고 반환합니다.
     * @param keyword 검색할 키워드
     * @return 키워드를 포함하는 상품들의 목록을 반환합니다.
     */
    List<Item> findAllByNameContainingOrderByPriceAsc(String keyword);
}
