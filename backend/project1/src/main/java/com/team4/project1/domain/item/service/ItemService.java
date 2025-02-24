package com.team4.project1.domain.item.service;

import com.team4.project1.domain.item.dto.ItemDto;
import com.team4.project1.domain.item.entity.Item;
import com.team4.project1.domain.item.repository.ItemRepository;
import com.team4.project1.global.exception.InsufficientStockException;
import com.team4.project1.global.exception.ItemNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ItemService {

    @Value("${file.upload-dir}")
    private String imageDir;

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
    
    public Page<ItemDto> searchAllItemsSortedBy(String sortBy, String keyword, Pageable pageable) {
        if ("price".equalsIgnoreCase(sortBy)) {
            return itemRepository.findAllByNameContainingOrderByPriceAsc(keyword, pageable).map(ItemDto::from);
        }
        else {
            return itemRepository.findAllByNameContainingOrderByNameAsc(keyword, pageable).map(ItemDto::from);
        }
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

    public Resource getItemImage(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));
        if(item.getImageUuid() == null) {
            return null;
        }
        try {
            Path filePath = Paths.get(imageDir).resolve(item.getImageUuidAsUri());
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            }
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public String addImageToItem(Long id, MultipartFile image) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));
        // 이미 존재하는 이미지를 삭제한다
        if (item.getImageUuid() != null) {
            deleteImageFromItem(item);
            item.setImageUuid(null);
        }
        UUID newUuid = UUID.randomUUID();
        String newImageName = newUuid + ".jpg";
        saveImageAs(image, newImageName);
        item.setImageUuid(newUuid);
        itemRepository.save(item);
        return newImageName;
    }

    private void deleteImageFromItem(Item item) {
        Path uploadPath = Paths.get(imageDir);
        Path filePath = uploadPath.resolve(item.getImageUuidAsUri());
        if (!Files.exists(filePath)) {
            throw new RuntimeException("파일이 존재하지 않습니다. (상품: %d, 경로: %s)"
                    .formatted(item.getId(), filePath.toString()));
        }
        try {
            Files.delete(filePath);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveImageAs(MultipartFile file, String fileName){
        Path uploadPath = Paths.get(imageDir);
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
