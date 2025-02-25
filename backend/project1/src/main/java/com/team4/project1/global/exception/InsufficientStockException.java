package com.team4.project1.global.exception;

/**
 * 재고 부족 예외를 처리하는 클래스입니다.
 * 상품의 재고가 부족할 때 발생하는 예외로, 예외 메시지에는 상품 ID와 현재 재고 수량이 포함됩니다.
 */
public class InsufficientStockException extends RuntimeException {

  /**
   * 재고 부족 예외를 생성하는 생성자입니다.
   * @param itemId 상품 ID
   * @param stock 현재 재고 수량
   */
  public InsufficientStockException(Long itemId, Integer stock) {
    super("상품의 재고가 부족합니다. (상품 ID: " + itemId + ", 현재 재고: " + stock + ")");
  }
}
