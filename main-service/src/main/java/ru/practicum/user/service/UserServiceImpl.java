package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.exception.UserNotExistException;
import ru.practicum.user.NewUserRequest;
import ru.practicum.user.UserDto;
import ru.practicum.user.mapper.UserDtoMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(NewUserRequest newUserRequest) {
        validateUser(newUserRequest);
        User newUser = UserDtoMapper.toUser(newUserRequest);
        log.info("Пользователь {} создан", newUser);
        return UserDtoMapper.toUserDto(userRepository.save(newUser));
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        List<UserDto> userDtos;
        Pageable page = PageRequest.of(from / size , size); // from / size , size
        if(ids == null) {
            userDtos = userRepository.findAll(page).getContent().stream()
                    .map(UserDtoMapper::toUserDto)
                    .collect(Collectors.toList());
        } else {
            userDtos = userRepository.findAllByIdIn(ids, page).getContent().stream()
                    .map(UserDtoMapper::toUserDto)
                    .collect(Collectors.toList());
        }
        log.info("Получен список пользователей с размером {}", userDtos.size());
        return userDtos;
    }

    @Override
    public void deleteUser(int id) {
        userRepository.findById((long) id).orElseThrow(() -> new UserNotExistException("Пользователь не найден"));
        log.info("Пользователь с айди {} удален", id);
        userRepository.deleteById((long)id);
    }

    private void validateUser(NewUserRequest newUserRequest) {
        if (newUserRequest.getName() == null || newUserRequest.getEmail() == null ||
                newUserRequest.getName().isEmpty() || newUserRequest.getEmail().isEmpty() ||
        newUserRequest.getName().isBlank() || newUserRequest.getEmail().isBlank()) {
            throw new ValidationException();
        }
    }
}
