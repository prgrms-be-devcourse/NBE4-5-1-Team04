package com.team4.project1.domain.item.dto;


import com.team4.project1.domain.item.entity.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Item 엔티티 DTO")
class ItemDtoTest {

    @Test
    @DisplayName("DTO로부터 Item 엔티티 인스턴스를 생성한다.")
    void testInstantiateEntityFromDto() {
        UUID uuid = UUID.randomUUID();
        Item item = new Item("SomeItem", 1337, 0, uuid);
        // Item 엔티티에 해당하는 DTO 생성
        ItemDto itemDto = ItemDto.from(item);
        // 생성한 DTO로 새 Item 엔티티 생성
        Item converted = itemDto.toEntity();

        assertThat(converted.getId()).isEqualTo(item.getId());
        assertThat(converted.getName()).isEqualTo(item.getName());
        assertThat(converted.getPrice()).isEqualTo(item.getPrice());
        assertThat(converted.getStock()).isEqualTo(item.getStock());
        assertThat(converted.getImageUuid()).isEqualTo(uuid);
    }
}