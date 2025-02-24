package com.team4.project1.domain.item.entity;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("Item 엔티티")
class ItemTest {

    @Test
    @DisplayName("이미지가 등록되어 있을때 이미지 UUID를 URI로 변환한다.")
    void testUuidToUriStringConversion() {
        Item item = new Item();
        UUID uuid = UUID.randomUUID();
        item.setImageUuid(uuid);

        assertThat(item.getImageUuidAsUri()).isEqualTo(uuid.toString() + ".jpg");
    }

    @Test
    @DisplayName("이미지가 등록되어 있지 않을때 이미지 URI로 빈 문자열을 반환한다.")
    void testNoUuidToEmptyStringConversion() {
        Item item = new Item();

        assertThat(item.getImageUuidAsUri()).isEmpty();
    }
}