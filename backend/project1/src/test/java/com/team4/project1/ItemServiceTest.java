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

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;  // 가짜 객체 (Mock)

    @InjectMocks
    private ItemService itemService; // itemRepository를 주입받을 실제 서비스 객체

    private Item item;

    @BeforeEach
    void setUp() {
        item = Item.builder()
                .name("Test Item")
                .price(1000)
                .build();
    }

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

    @Test
    @DisplayName("존재하지 않는 ID의 아이템을 조회하면 예외가 발생한다.")
    void getItemById_NotFound() {
        // Given
        when(itemRepository.findById(2L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ItemNotFoundException.class, () -> itemService.getItemById(2L));

        verify(itemRepository, times(1)).findById(2L);
    }

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
