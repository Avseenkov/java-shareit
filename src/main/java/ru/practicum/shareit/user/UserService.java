package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    UserStorage userStorage;

    public UserDto createUser(UserDto user) {
        emailIsExist(user.getEmail());
        return getUser(userStorage.add(UserMapper.UserFromUserDto(user)));
    }

    public UserDto getUser(long id) {
        return UserMapper.UserDtoFromUser(getUserFromStorage(id));
    }

    public UserDto updateUser(long id, UserDto userDto) {
        User user = getUserFromStorage(id);
        if (userDto.getName() != null) user.setName(userDto.getName());
        if (userDto.getEmail() != null) {
            emailIsExist(userDto.getEmail());
            user.setEmail(userDto.getEmail());
        }
        userStorage.update(user);
        return UserMapper.UserDtoFromUser(getUserFromStorage(user.getId()));
    }

    public void deleteUser(long id) {
        userStorage.delete(id);
    }

    public void emailIsExist(String email) {
        userStorage.emailIsExist(email);
    }

    public List<UserDto> getAllUsers() {
        return userStorage.getAll().stream().map(user -> UserMapper.UserDtoFromUser(user)).toList();
    }

    private User getUserFromStorage(long id) {
        return userStorage.get(id).orElseThrow(() -> new NotFoundException("User with id = %s not found".formatted(id)));
    }
}
