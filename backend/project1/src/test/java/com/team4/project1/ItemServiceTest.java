package com.team4.project1;

import com.team4.project1.domain.item.dto.ItemDto;
import com.team4.project1.domain.item.entity.Item;
import com.team4.project1.domain.item.entity.ItemSortType;
import com.team4.project1.domain.item.repository.ItemRepository;
import com.team4.project1.domain.item.service.ItemService;
import com.team4.project1.global.exception.ItemNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.any;
/**
 * {@link ItemService} 클래스의 단위 테스트.
 * {@link ItemService}의 메서드들이 예상대로 동작하는지 확인.
 */
@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    private List<Item> items = new ArrayList<>();

    @BeforeEach
    void setUp() {
        items.add(new Item("Aardvark", 1, 1));
    }

    /**
     * 가격 기준 정렬 테스트.
     */
    @Test
    @DisplayName("정렬 기준(price)에 따라 아이템을 정렬")
    void getAllItemsSortedByPrice() {
        Pageable pageable = Pageable.ofSize(20);
        given(itemRepository.findAllByNameContainingOrderByPriceAsc("Test", pageable))
                .willReturn(Page.empty());
        Page<ItemDto> itemPages = itemService.searchAllItemsSortedBy(ItemSortType.PRICE, "Test", pageable);

        assertThat(itemPages).isEmpty();
        then(itemRepository).should().findAllByNameContainingOrderByPriceAsc("Test", pageable);
    }

    /**
     * 이름 기준 정렬 테스트.
     */
    @Test
    @DisplayName("정렬 기준(name)에 따라 아이템을 정렬")
    void getAllItemsSortedByName() {
        Pageable pageable = Pageable.ofSize(20);
        given(itemRepository.findAllByNameContainingOrderByNameAsc("Test", pageable))
                .willReturn(Page.empty());
        Page<ItemDto> itemPages = itemService.searchAllItemsSortedBy(ItemSortType.NAME, "Test", pageable);

        assertThat(itemPages).isEmpty();
        then(itemRepository).should().findAllByNameContainingOrderByNameAsc("Test", pageable);
    }

    /**
     * 특정 ID 아이템 조회 테스트.
     */
    @Test
    @DisplayName("특정 ID의 아이템 조회")
    void getItemById() {
        given(itemRepository.findById(0L)).willReturn(Optional.of(items.getFirst()));
        ItemDto foundItem = itemService.getItemById(0L);
        assertThat(foundItem).isNotNull();
        assertThat(foundItem.getName()).isEqualTo(items.getFirst().getName());
        then(itemRepository).should().findById(0L);
    }

    /**
     * 존재하지 않는 ID 아이템 조회 시 예외 발생 테스트.
     */
    @Test
    @DisplayName("존재하지 않는 ID 조회 시 예외 발생")
    void getItemByIdNotFound() {
        given(itemRepository.findById(0L)).willReturn(Optional.empty());
        assertThrows(ItemNotFoundException.class, () -> itemService.getItemById(0L));
        then(itemRepository).should().findById(0L);
    }

    /**
     * 새 아이템 추가 테스트.
     */
    @Test
    @DisplayName("새로운 아이템 추가")
    void addItem() {
        given(itemRepository.save(any(Item.class))).willReturn(items.getFirst());
        Item mockItem = items.getFirst();
        Item newItem = itemService.addItem(mockItem.getName(), mockItem.getPrice(), mockItem.getStock());
        assertThat(newItem).isNotNull();
        assertThat(newItem.getName()).isEqualTo(mockItem.getName());
        assertThat(newItem.getPrice()).isEqualTo(mockItem.getPrice());
        assertThat(newItem.getStock()).isEqualTo(mockItem.getStock());
        then(itemRepository).should().save(any(Item.class));
    }

    /**
     * 전체 아이템 개수 조회 테스트.
     */
    @Test
    @DisplayName("전체 아이템 개수 조회")
    void countItems() {
        given(itemRepository.count()).willReturn(5L);
        long itemCount = itemService.count();
        assertThat(itemCount).isEqualTo(5);
        then(itemRepository).should().count();
    }
}