package ru.practicum.exception;

public class UserIsNotOwner extends RuntimeException {
    public UserIsNotOwner(String mess) {
        super(mess);
    }
}
