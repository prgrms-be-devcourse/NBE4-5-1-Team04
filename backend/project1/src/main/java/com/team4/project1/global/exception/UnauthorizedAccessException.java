package com.team4.project1.global.exception;

public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String message) {

      super(message);
    }
}
