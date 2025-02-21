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
class ApiV1ItemControllerTest {

    @Mock
    private ItemRepository itemRepository;  // 가짜 객체 (Mock)

    @InjectMocks
    private ItemService itemService; // itemRepository를 주입받을 실제 서비스 객체

    private Item item;

    @BeforeEach
    void setUp() {
        item = Item.builder()
                .id(1)
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
        when(itemRepository.findAllByOrderByPriceAsc()).thenReturn(List.of(item));

        // When
        List<ItemDto> items = itemService.getAllItemsSorted("price");

        // Then
        assertThat(items).isNotEmpty();
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getPrice()).isEqualTo(1000);

        verify(itemRepository, times(1)).findAllByOrderByPriceAsc();
    }

    @Test
    @DisplayName("정렬 기준(name)에 따라 아이템을 정렬하여 조회할 수 있다.")
    void getAllItemsSortedByName() {
        // Given
        when(itemRepository.findAllByOrderByNameAsc()).thenReturn(List.of(item));

        // When
        List<ItemDto> items = itemService.getAllItemsSorted("name");

        // Then
        assertThat(items).isNotEmpty();
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Test Item");

        verify(itemRepository, times(1)).findAllByOrderByNameAsc();
    }

    @Test
    @DisplayName("특정 ID의 아이템을 조회할 수 있다.")
    void getItemById() {
        // Given
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));

        // When
        Optional<ItemDto> foundItem = itemService.getItemById(1);

        // Then
        assertThat(foundItem).isPresent();
        assertThat(foundItem.get().getName()).isEqualTo("Test Item");

        verify(itemRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("존재하지 않는 ID의 아이템을 조회하면 예외가 발생한다.")
    void getItemById_NotFound() {
        // Given
        when(itemRepository.findById(2)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ItemNotFoundException.class, () -> itemService.getItemById(2));

        verify(itemRepository, times(1)).findById(2);
    }

    @Test
    @DisplayName("새로운 아이템을 추가할 수 있다.")
    void addItem() {
        // Given
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        // When
        Item newItem = itemService.addItem("Test Item", 1000);

        // Then
        assertThat(newItem).isNotNull();
        assertThat(newItem.getName()).isEqualTo("Test Item");
        assertThat(newItem.getPrice()).isEqualTo(1000);

        verify(itemRepository, times(1)).save(any(Item.class));
    }
}
