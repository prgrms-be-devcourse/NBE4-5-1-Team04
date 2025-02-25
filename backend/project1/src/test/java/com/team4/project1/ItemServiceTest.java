package com.team4.project1;

import com.team4.project1.domain.item.dto.ItemDto;
import com.team4.project1.domain.item.entity.Item;
import com.team4.project1.domain.item.repository.ItemRepository;
import com.team4.project1.domain.item.service.ItemService;
import com.team4.project1.global.exception.ItemNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;
/**
 * {@link ItemService} 클래스에 대한 단위 테스트입니다.
 * 이 클래스는 {@link ItemService}의 메서드들이 예상대로 동작하는지 확인하는 테스트 메서드들을 포함하고 있습니다.
 */
@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;  // 가짜 객체 (Mock)

    @InjectMocks
    private ItemService itemService; // itemRepository를 주입받을 실제 서비스 객체

    private List<Item> items = new ArrayList<>();

    @BeforeEach
    void setUp() {
        items.add(new Item("Aardvark", 1, 1));
    }
    /**
     * 아이템을 가격 기준으로 정렬하여 조회하는 메서드에 대한 테스트입니다.
     * 정렬 기준이 'price'일 때, 아이템 목록이 가격에 따라 올바르게 정렬되어 반환되는지 확인합니다.
     */
    @Test
    @DisplayName("정렬 기준(price)에 따라 아이템을 정렬하여 조회할 수 있다.")
    void getAllItemsSortedByPrice() {
        // Given
        Pageable pageable = Pageable.ofSize(20);
        given(itemRepository.findAllByNameContainingOrderByPriceAsc("Test", pageable))
                .willReturn(Page.empty());

        // When
        Page<ItemDto> itemPages = itemService.searchAllItemsSortedBy("price", "Test", pageable);

        // Then
        assertThat(itemPages).isEmpty();
        then(itemRepository).should().findAllByNameContainingOrderByPriceAsc("Test", pageable);
    }

    /**
     * 아이템을 이름 기준으로 정렬하여 조회하는 메서드에 대한 테스트입니다.
     * 정렬 기준이 'name'일 때, 아이템 목록이 이름에 따라 올바르게 정렬되어 반환되는지 확인합니다.
     */
    @Test
    @DisplayName("정렬 기준(name)에 따라 아이템을 정렬하여 조회할 수 있다.")
    void getAllItemsSortedByName() {
        // Given
        Pageable pageable = Pageable.ofSize(20);
        given(itemRepository.findAllByNameContainingOrderByNameAsc("Test", pageable))
                .willReturn(Page.empty());

        // When
        Page<ItemDto> itemPages = itemService.searchAllItemsSortedBy("name", "Test", pageable);

        // Then
        assertThat(itemPages).isEmpty();
        then(itemRepository).should().findAllByNameContainingOrderByNameAsc("Test", pageable);
    }

    /**
     * 특정 ID의 아이템을 조회하는 메서드에 대한 테스트입니다.
     * 아이템 ID로 아이템을 조회하고, 올바른 아이템이 반환되는지 확인합니다.
     */
    @Test
    @DisplayName("존재하는 특정 ID의 아이템을 조회할 수 있다.")
    void getItemById() {
        // Given
        given(itemRepository.findById(0L)).willReturn(Optional.of(items.getFirst()));

        // When
        ItemDto foundItem = itemService.getItemById(0L);  // 반환값을 ItemDto로 받기

        // Then
        assertThat(foundItem).isPresent();
        assertThat(foundItem.getName()).isEqualTo(items.getFirst().getName());
        then(itemRepository).should().findById(0L);
    }

    /**
     * 존재하지 않는 ID의 아이템을 조회할 때 발생하는 예외를 테스트합니다.
     * 존재하지 않는 아이템 ID로 조회를 시도할 때 {@link ItemNotFoundException} 예외가 발생하는지 확인합니다.
     */
    @Test
    @DisplayName("존재하지 않는 ID의 아이템을 조회하면 예외가 발생한다.")
    void getItemByIdNotFound() {
        // Given
        given(itemRepository.findById(0L)).willReturn(Optional.empty());

        // When & Then
        assertThrows(ItemNotFoundException.class, () -> itemService.getItemById(0L));
        then(itemRepository).should().findById(0L);
    }
    /**
     * 새로운 아이템을 추가하는 메서드에 대한 테스트입니다.
     * 새로운 아이템을 추가하고, 추가된 아이템이 반환되는지 확인합니다.
     */
    @Test
    @DisplayName("새로운 아이템을 추가할 수 있다.")
    void addItem() {
        // Given
        given(itemRepository.save(any(Item.class))).willReturn(items.getFirst());

        // When
        Item mockItem = items.getFirst();
        Item newItem = itemService.addItem(mockItem.getName(),
                mockItem.getPrice(),mockItem.getStock());

        // Then
        assertThat(newItem).isNotNull();
        assertThat(newItem.getName()).isEqualTo(mockItem.getName());
        assertThat(newItem.getPrice()).isEqualTo(mockItem.getPrice());
        assertThat(newItem.getStock()).isEqualTo(mockItem.getStock());
        then(itemRepository).should().save(any(Item.class));
    }
    /**
     * 전체 아이템 개수를 조회하는 메서드에 대한 테스트입니다.
     * 아이템 수를 조회하고, 결과가 예상한 값과 일치하는지 확인합니다.
     */
    @Test
    @DisplayName("전체 아이템 개수를 조회할 수 있다.")
    void countItems() {
        // Given
        given(itemRepository.count()).willReturn(5L);

        // When
        long itemCount = itemService.count();

        // Then
        assertThat(itemCount).isEqualTo(5);
        then(itemRepository).should().count();
    }
}