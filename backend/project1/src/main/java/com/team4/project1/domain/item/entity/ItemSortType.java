package com.team4.project1.domain.item.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ItemSortType {
    NAME(Sort.by(Sort.Direction.ASC, "name")),
    PRICE(Sort.by(Sort.Direction.ASC, "price"));

    private final Sort sort;

    ItemSortType(Sort sort) {
        this.sort = sort;
    }

    public Sort toSort() {
        return sort;
    }

    public static ItemSortType fromString(String value) {
        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(value))
                .findFirst()
                .orElse(NAME);  // 기본값은 NAME
    }
}
