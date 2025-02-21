package com.team4.project1;

import com.team4.project1.domain.item.dto.ItemDto;
import com.team4.project1.domain.item.entity.Item;
import com.team4.project1.domain.item.repository.ItemRepository;
import com.team4.project1.domain.item.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
}
