package ru.practicum.exception;

public class CommentNotExistException extends RuntimeException {
    public CommentNotExistException(String mess) {
        super(mess);
    }
}
