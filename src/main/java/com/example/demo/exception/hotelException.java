package com.example.demo.exception;

public class hotelException extends RuntimeException{
    public hotelException(String message) {
        super(message);
    }

    public hotelException(Throwable cause) {
        super(cause);
    }
}
