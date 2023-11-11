package ru.practicum.user.service;

import ru.practicum.user.NewUserRequest;
import ru.practicum.user.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(NewUserRequest newUserRequest);

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    void deleteUser(long id);
}
