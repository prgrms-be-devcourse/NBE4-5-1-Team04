package com.team4.project1;

import com.team4.project1.domain.item.dto.ItemDto;
import com.team4.project1.domain.item.entity.Item;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    private Item item;
    /**
     * 각 테스트가 실행되기 전에 호출되는 메서드입니다.
     * 테스트를 위해 사용할 아이템 객체를 초기화합니다.
     */
    @BeforeEach
    void setUp() {
        item = Item.builder()
                .id(1L)
                .name("Test Item")
                .price(1000)
                .build();
    }
    /**
     * 모든 아이템을 조회하는 메서드에 대한 테스트입니다.
     * 아이템 목록을 조회하고, 결과가 비어있지 않으며 예상한 아이템이 포함되는지 확인합니다.
     */
    @Test
    @DisplayName("모든 아이템을 조회할 수 있다.")
    void getAllItems() {
        // Given
        when(itemRepository.findAll()).thenReturn(List.of(item));

        // When
        List<ItemDto> items = itemService.getAllItems();

        // Then
        assertThat(items).isNotEmpty();
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Test Item");

        verify(itemRepository, times(1)).findAll();
    }
    /**
     * 아이템을 가격 기준으로 정렬하여 조회하는 메서드에 대한 테스트입니다.
     * 정렬 기준이 'price'일 때, 아이템 목록이 가격에 따라 올바르게 정렬되어 반환되는지 확인합니다.
     */
    @Test
    @DisplayName("정렬 기준(price)에 따라 아이템을 정렬하여 조회할 수 있다.")
    void getAllItemsSortedByPrice() {
        // Given
        when(itemRepository.findAllByNameContainingOrderByPriceAsc("Test")).thenReturn(List.of(item));

        // When
        List<ItemDto> items = itemService.searchAllItemsSortedBy("price", "Test");

        // Then
        assertThat(items).isNotEmpty();
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getPrice()).isEqualTo(1000);

        verify(itemRepository, times(1)).findAllByNameContainingOrderByPriceAsc("Test");
    }

    /**
     * 아이템을 이름 기준으로 정렬하여 조회하는 메서드에 대한 테스트입니다.
     * 정렬 기준이 'name'일 때, 아이템 목록이 이름에 따라 올바르게 정렬되어 반환되는지 확인합니다.
     */
    @Test
    @DisplayName("정렬 기준(name)에 따라 아이템을 정렬하여 조회할 수 있다.")
    void getAllItemsSortedByName() {
        // Given
        when(itemRepository.findAllByNameContainingOrderByNameAsc("Test")).thenReturn(List.of(item));

        // When
        List<ItemDto> items = itemService.searchAllItemsSortedBy("name", "Test");

        // Then
        assertThat(items).isNotEmpty();
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Test Item");

        verify(itemRepository, times(1)).findAllByNameContainingOrderByNameAsc("Test");
    }

    /**
     * 특정 ID의 아이템을 조회하는 메서드에 대한 테스트입니다.
     * 아이템 ID로 아이템을 조회하고, 올바른 아이템이 반환되는지 확인합니다.
     */
    @Test
    @DisplayName("특정 ID의 아이템을 조회할 수 있다.")
    void getItemById() {
        // Given
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        // When
        Optional<ItemDto> foundItem = itemService.getItemById(1L);

        // Then
        assertThat(foundItem).isPresent();
        assertThat(foundItem.get().getName()).isEqualTo("Test Item");

        verify(itemRepository, times(1)).findById(1L);
    }

    /**
     * 존재하지 않는 ID의 아이템을 조회할 때 발생하는 예외를 테스트합니다.
     * 존재하지 않는 아이템 ID로 조회를 시도할 때 {@link ItemNotFoundException} 예외가 발생하는지 확인합니다.
     */
    @Test
    @DisplayName("존재하지 않는 ID의 아이템을 조회하면 예외가 발생한다.")
    void getItemById_NotFound() {
        // Given
        when(itemRepository.findById(2L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ItemNotFoundException.class, () -> itemService.getItemById(2L));

        verify(itemRepository, times(1)).findById(2L);
    }
    /**
     * 새로운 아이템을 추가하는 메서드에 대한 테스트입니다.
     * 새로운 아이템을 추가하고, 추가된 아이템이 반환되는지 확인합니다.
     */
    @Test
    @DisplayName("새로운 아이템을 추가할 수 있다.")
    void addItem() {
        // Given
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        // When
        Item newItem = itemService.addItem("Test Item", 1000,100);

        // Then
        assertThat(newItem).isNotNull();
        assertThat(newItem.getName()).isEqualTo("Test Item");
        assertThat(newItem.getPrice()).isEqualTo(1000);

        verify(itemRepository, times(1)).save(any(Item.class));
    }
    /**
     * 전체 아이템 개수를 조회하는 메서드에 대한 테스트입니다.
     * 아이템 수를 조회하고, 결과가 예상한 값과 일치하는지 확인합니다.
     */
    @Test
    @DisplayName("전체 아이템 개수를 조회할 수 있다.")
    void countItems() {
        // Given
        when(itemRepository.count()).thenReturn(5L);

        // When
        long itemCount = itemService.count();

        // Then
        assertThat(itemCount).isEqualTo(5);

        verify(itemRepository, times(1)).count();
    }
}
