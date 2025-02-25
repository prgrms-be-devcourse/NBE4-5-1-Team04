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

/**
 * {@link Item} 엔티티에 대한 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * 아이템의 추가, 수정, 삭제, 재고 감소, 검색 등 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
public class ItemService {

    @Value("${file.upload-dir}")
    private String imageDir;

    private final ItemRepository itemRepository;

    /**
     * 상품의 재고를 차감하는 메서드입니다.
     * 상품이 존재하지 않으면 {@Link ItemNotFoundException}이 발생합니다.
     * * 주어진 수량만큼 재고가 차감되고, 재고가 부족할 경우 {@Link InsufficientStockException}이 발생합니다.
     * @param itemId 차감할 상품의 ID
     * @param quantity 차감할 수량
     * @throws ItemNotFoundException 상품을 찾을 수 없는 경우 예외 발생
     * @throws InsufficientStockException 재고가 부족한 경우 예외 발생
     */
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

    /**
     * 주어진 키워드로 상품을 검색하고, 주어진 정렬 기준에 따라서 상품을 정렬하여 반환합니다.
     * @param sortBy 정렬 기준(price 또는 name으로 정렬합니다.)
     * @param keyword 검색할 키워드
     * @return 검색된 상품의 DTO 목록을 반환합니다.
     */
    public Page<ItemDto> searchAllItemsSortedBy(String sortBy, String keyword, Pageable pageable) {
        if ("price".equalsIgnoreCase(sortBy)) {
            return itemRepository.findAllByNameContainingOrderByPriceAsc(keyword, pageable).map(ItemDto::from);
        }
        else {
            return itemRepository.findAllByNameContainingOrderByNameAsc(keyword, pageable).map(ItemDto::from);
        }
    }

    // TODO: 메소드 삭제 및 관련 코드 리팩토링
    /**
     * 모든 상품을 조회하여 반환하는 메서드입니다.
     * @return 상품 목록의 DTO를 반환합니다.
     */
    @Deprecated
    public List<ItemDto> getAllItems() {
        return itemRepository.findAll().stream()
                .map(ItemDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 주어진 ID로 상품을 조회하여 DTO 형태로 반환합니다.
     * @param itemId 조회할 상품의 ID
     * @return 상품의 DTO를 반환합니다.
     * @throws ItemNotFoundException 상품을 찾을 수 없는 경우 예외가 발생합니다.
     */
    public Optional<ItemDto> getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
        return Optional.of(ItemDto.from(item));
    }

    /**
     * 상품의 총 개수를 반환하는 메서드입니다.
     * @return 상품의 총 개수를 반환합니다.
     */
    public long count() {
        return itemRepository.count();
    }

    /**
     * 새로운 상품을 생성하는 메서드입니다.
     * @param name 상품의 이름
     * @param price 상품의 가격
     * @param stock 상품의 재고
     * @return 생성된 상품을 반환합니다.
     */
    public Item addItem(String name, Integer price, Integer stock) {
        Item item = Item.builder()
                .name(name)
                .price(price)
                .stock(stock)
                .build();

        return itemRepository.save(item);
    }
    /**
     * 주어진 Id에 해당하는 상품을 업데이트하는 메서드입니다.
     * @param id 업데이트할 상품의 Id
     * @param itemDto 업데이트할 상품의 정보가 담긴 DTO
     * @return 수정된 상품의 DTO를 반환합니다.
     * @throws ItemNotFoundException 상품을 찾을 수 없는 경우 예외 발생합니다.
     */
    public ItemDto updateItem(Long id, ItemDto itemDto) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));  // 아이템을 찾지 못하면 예외를 던짐

        item.setName(itemDto.getName());
        item.setPrice(itemDto.getPrice());

        Item updatedItem = itemRepository.save(item);
        return ItemDto.from(updatedItem);  // 수정된 아이템을 반환
    }

    /**
     * 주어진 ID에 해당하는 상품을 삭제하는 메서드입니다.
     *
     * @param id 삭제할 상품의 ID
     * @throws ItemNotFoundException 아이템을 찾을 수 없는 경우 예외가 발생합니다.
     */
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

    private void saveImageAs(MultipartFile file, String fileName) {
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
