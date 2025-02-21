package com.team4.project1.global.exception;

public class ItemAlreadyExistsException extends RuntimeException {
  public ItemAlreadyExistsException(String name) {
    super("이미 존재하는 상품입니다. (이름: " + name + ")");
  }
}