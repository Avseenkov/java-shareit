package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exception.EmailExistException;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    UserStorage userStorage;

    @Override
    @Transactional
    public UserDto createUser(UserDto user) {
        return getUser(userStorage.save(UserMapper.userFromUserDto(user)).getId());
    }


    @Override
    @Transactional(readOnly = true)
    public UserDto getUser(long id) {
        return UserMapper.userDtoFromUser(userStorage.getUserFromStorage(id));
    }

    @Override
    @Transactional
    public UserDto updateUser(long id, UserDto userDto) {
        User user = userStorage.getUserFromStorage(id);
        if (userDto.getName() != null) user.setName(userDto.getName());
        if (userDto.getEmail() != null) {
            emailIsExist(userDto.getEmail());
            user.setEmail(userDto.getEmail());
        }
        User updatedUser = userStorage.save(user);
        return UserMapper.userDtoFromUser(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        userStorage.delete(userStorage.getReferenceById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public void emailIsExist(String email) {
        userStorage.findByEmailIgnoreCase(email).ifPresent(user -> {
            throw new EmailExistException(String.format("%s is already exist", email));
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userStorage.findAll().stream().map(UserMapper::userDtoFromUser).collect(Collectors.toList());
    }

}
