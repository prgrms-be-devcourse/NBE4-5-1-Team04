package com.team4.project1.domain.item.service;

import com.team4.project1.domain.customer.dto.CustomerDto;
import com.team4.project1.domain.customer.entity.Customer;
import com.team4.project1.domain.item.dto.ItemDto;
import com.team4.project1.domain.item.entity.Item;
import com.team4.project1.domain.item.repository.ItemRepository;
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

    public List<ItemDto> getAllItemsSorted(String sortBy) {
        List<Item> items;

        if ("price".equalsIgnoreCase(sortBy)) {
            items = itemRepository.findAllByOrderByPriceAsc();
        } else if ("name".equalsIgnoreCase(sortBy)) {
            items = itemRepository.findAllByOrderByNameAsc();
        } else {
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


    public Item updateItem(Long id, ItemDto itemDto) {
        // 1. itemRepository에서 id에 해당하는 Item을 찾아옵니다.
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));  // 만약 찾지 못하면 예외를 던집니다.

        // 2. Item 객체를 업데이트합니다.
        item.setName(itemDto.getName());  // ItemDto의 name을 사용하여 Item의 name을 설정합니다.
        item.setPrice(itemDto.getPrice());  // ItemDto의 price를 사용하여 Item의 price를 설정합니다.

        // 3. 업데이트된 Item 객체를 데이터베이스에 저장합니다.
        return itemRepository.save(item);  // 수정된 Item을 저장하고 반환합니다.
    }
}