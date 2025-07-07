package com.example.exception;

/**
 * ユーザーが既に存在する場合の例外
 */
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}