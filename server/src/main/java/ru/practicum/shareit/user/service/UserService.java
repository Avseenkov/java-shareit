package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto user);

    UserDto getUser(long id);

    UserDto updateUser(long id, UserDto userDto);

    void deleteUser(long id);

    void emailIsExist(String email, Long id);

    List<UserDto> getAllUsers();
}
