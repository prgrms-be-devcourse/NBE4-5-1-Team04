package com.team4.project1.global.exception;


public class InvalidOrderQuantityException extends RuntimeException {


    public InvalidOrderQuantityException(String message) {
        super(message);
    }
}
