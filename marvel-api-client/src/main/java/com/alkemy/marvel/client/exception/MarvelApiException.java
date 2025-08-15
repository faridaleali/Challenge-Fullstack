package com.alkemy.marvel.client.exception;

public class MarvelApiException extends RuntimeException {
    
    public MarvelApiException(String message) {
        super(message);
    }
    
    public MarvelApiException(String message, Throwable cause) {
        super(message, cause);
    }
}