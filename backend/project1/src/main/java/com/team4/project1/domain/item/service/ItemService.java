package com.team4.project1.domain.item.service;

import com.team4.project1.domain.item.dto.ItemDto;
import com.team4.project1.domain.item.entity.Item;
import com.team4.project1.domain.item.repository.ItemRepository;
import com.team4.project1.global.exception.InsufficientStockException;
import com.team4.project1.global.exception.ItemNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;


    public void reduceStock(Long itemId, int quantity) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId)); // 아이템이 없으면 예외 던짐

        if (item.getStock() < quantity) {
            // 재고 부족 시 예외 던짐
            throw new InsufficientStockException(itemId, item.getStock());
        }

        item.setStock(item.getStock() - quantity); // 재고 차감
        itemRepository.save(item); // 변경된 아이템 저장
    }


    public List<ItemDto> searchAllItemsSortedBy(String sortBy, String keyword) {
        List<Item> items;

        if ("price".equalsIgnoreCase(sortBy)) {
            items = itemRepository.findAllByNameContainingOrderByPriceAsc(keyword);
        }
        else if ("name".equalsIgnoreCase(sortBy)) {
            items = itemRepository.findAllByNameContainingOrderByNameAsc(keyword);
        }
        else {
            items = itemRepository.findAll();
        }

        return items.stream()
                .map(ItemDto::from)
                .collect(Collectors.toList());
    }


    public List<ItemDto> getAllItems() {
        return itemRepository.findAll().stream()
                .map(ItemDto::from)
                .collect(Collectors.toList());
    }


    public Optional<ItemDto> getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
        return Optional.of(ItemDto.from(item));
    }


    public long count() {
        return itemRepository.count();
    }


    public Item addItem(String name, Integer price, Integer stock) {
        Item item = Item.builder()
                .name(name)
                .price(price)
                .stock(stock)
                .build();

        return itemRepository.save(item);
    }


    public ItemDto updateItem(Long id, ItemDto itemDto) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));  // 아이템을 찾지 못하면 예외를 던짐

        item.setName(itemDto.getName());
        item.setPrice(itemDto.getPrice());

        Item updatedItem = itemRepository.save(item);
        return ItemDto.from(updatedItem);  // 수정된 아이템을 반환
    }


    public void deleteItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));
        itemRepository.delete(item);
    }
}
