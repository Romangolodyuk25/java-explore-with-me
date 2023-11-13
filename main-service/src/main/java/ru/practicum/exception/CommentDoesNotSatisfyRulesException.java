package ru.practicum.exception;

public class CommentDoesNotSatisfyRulesException extends RuntimeException {
    public CommentDoesNotSatisfyRulesException(String mess) {
        super(mess);
    }
}
