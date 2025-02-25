package com.team4.project1.domain.item.service;

import com.team4.project1.domain.item.dto.ItemDto;
import com.team4.project1.domain.item.entity.Item;
import com.team4.project1.domain.item.entity.ItemSortType;
import com.team4.project1.domain.item.repository.ItemRepository;
import com.team4.project1.global.exception.InsufficientStockException;
import com.team4.project1.global.exception.ItemNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemService {

    @Value("${file.upload-dir}")
    private String imageDir;

    private final ItemRepository itemRepository;

    /**
     * 상품의 재고를 차감하는 메서드입니다.
     * @throws ItemNotFoundException 상품을 찾을 수 없을 경우 발생
     * @throws InsufficientStockException 재고가 부족할 경우 발생
     */
    public void reduceStock(Long itemId, int quantity) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        if (item.getStock() < quantity) {
            throw new InsufficientStockException(itemId, item.getStock());
        }

        item.setStock(item.getStock() - quantity);
        itemRepository.save(item);
    }

    /**
     * 키워드로 상품을 검색하고, 지정된 정렬 기준으로 정렬하여 반환합니다.
     * @param sortType 정렬 기준 (ItemSortType Enum)
     * @param keyword 검색할 키워드
     * @param pageable 페이징 정보
     * @return 검색된 상품의 DTO 목록 반환
     */
    public Page<ItemDto> searchAllItemsSortedBy(
            ItemSortType sortType, String keyword, Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortType.toSort());

        return itemRepository
                .findAllByNameContaining(keyword, sortedPageable)
                .map(ItemDto::from);
    }


    /**
     * 주어진 ID로 상품을 조회하여 DTO 형태로 반환합니다.
     * @throws ItemNotFoundException 상품을 찾을 수 없을 경우 발생
     */
    public Optional<ItemDto> getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
        return Optional.of(ItemDto.from(item));
    }

    /**
     * 상품의 총 개수를 반환하는 메서드입니다.
     */
    public long count() {
        return itemRepository.count();
    }

    /**
     * 새로운 상품을 추가하는 메서드입니다.
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
     * 주어진 ID에 해당하는 상품을 업데이트하는 메서드입니다.
     * @throws ItemNotFoundException 상품을 찾을 수 없는 경우 발생
     */
    public ItemDto updateItem(Long id, ItemDto itemDto) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));

        item.setName(itemDto.getName());
        item.setPrice(itemDto.getPrice());

        Item updatedItem = itemRepository.save(item);
        return ItemDto.from(updatedItem);
    }

    /**
     * 주어진 ID에 해당하는 상품을 삭제하는 메서드입니다.
     * @throws ItemNotFoundException 상품을 찾을 수 없는 경우 발생
     */
    public void deleteItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));
        itemRepository.delete(item);
    }

    /**
     * 상품 이미지 리소스를 가져오는 메서드입니다.
     */
    public Resource getItemImage(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));

        if (item.getImageUuid() == null) return null;

        try {
            Path filePath = Paths.get(imageDir).resolve(item.getImageUuidAsUri());
            Resource resource = new UrlResource(filePath.toUri());
            return resource.exists() ? resource : null;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 상품에 이미지를 추가하는 메서드입니다.
     */
    public String addImageToItem(Long id, MultipartFile image) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));

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

    /**
     * 상품의 기존 이미지를 삭제하는 메서드입니다.
     */
    private void deleteImageFromItem(Item item) {
        Path filePath = Paths.get(imageDir).resolve(item.getImageUuidAsUri());
        if (!Files.exists(filePath)) {
            throw new RuntimeException("파일이 존재하지 않습니다. (상품: %d, 경로: %s)"
                    .formatted(item.getId(), filePath.toString()));
        }
        try {
            Files.delete(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 이미지를 저장하는 메서드입니다.
     */
    private void saveImageAs(MultipartFile file, String fileName) {
        Path uploadPath = Paths.get(imageDir);
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
