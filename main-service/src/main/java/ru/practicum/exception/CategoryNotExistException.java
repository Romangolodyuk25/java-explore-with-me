package ru.practicum.exception;

public class CategoryNotExistException extends RuntimeException {
    public CategoryNotExistException(String mess) {
        super(mess);
    }
}
