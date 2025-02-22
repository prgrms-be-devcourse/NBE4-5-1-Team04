package com.team4.project1.global.exception;

public class InsufficientStockException extends RuntimeException {
  public InsufficientStockException(Long itemId, Integer stock) {
    super("상품의 재고가 부족합니다. (상품 ID: " + itemId + ", 현재 재고: " + stock + ")");
  }
}