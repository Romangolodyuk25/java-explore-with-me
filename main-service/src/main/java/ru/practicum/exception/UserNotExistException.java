package ru.practicum.exception;

public class UserNotExistException extends RuntimeException {
    public UserNotExistException(String mess) {
        super(mess);
    }

}
