package com.team4.project1.domain.item.entity;

import org.springframework.data.domain.Sort;

import java.util.Arrays;

public enum ItemSortType {
    NAME("name"),
    PRICE("price");

    private final String fieldName;

    ItemSortType(String fieldName) {
        this.fieldName = fieldName;
    }

    public Sort getSort(Sort.Direction direction) {
        return Sort.by(direction, this.fieldName);
    }

    public static ItemSortType fromString(String value) {
        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 정렬 기준: " + value));
    }
}
