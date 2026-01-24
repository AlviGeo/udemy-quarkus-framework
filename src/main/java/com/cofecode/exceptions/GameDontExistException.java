package com.cofecode.exceptions;

public class GameDontExistException extends RuntimeException {
    public GameDontExistException(String message) {
        super(message);
    }
    public GameDontExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
