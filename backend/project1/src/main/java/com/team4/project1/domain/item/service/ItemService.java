package com.team4.project1.domain.item.service;

import com.team4.project1.domain.item.entity.Item;
import com.team4.project1.domain.item.dto.ItemDto;
import com.team4.project1.domain.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public List<ItemDto> getAllItems() {
        return itemRepository.findAll().stream()
                .map(ItemDto::from)
                .sorted(Comparator.comparingInt(ItemDto::getprice))
                .collect(Collectors.toList());
    }

    public Optional<ItemDto> getItemById(Integer itemId) {
        Item item = itemRepository.findById(itemId).orElse(null);
        if (item == null) { return Optional.empty(); }
        return Optional.of(ItemDto.from(item));
    }

    public long count() {
        return itemRepository.count();
    }

    public Item addItem(String name, Integer price) {
        Item item = Item.builder()
                .name(name)
                .price(price)
                .build();

        return itemRepository.save(item);
    }
}
