package com.example.exception;

/**
 * リソースが見つからない場合の例外
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}