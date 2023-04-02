package ru.practicum.shareit.user.storage;

import org.springframework.context.annotation.Lazy;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;


public class UserStorageCustomImpl implements UserStorageCustom {

    private final UserStorage userStorage;

    public UserStorageCustomImpl(@Lazy UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User getUserFromStorage(Long id) {
        return userStorage.findById(id).orElseThrow(() -> new NotFoundException(String.format("User with id = %s not found", id)));
    }
}
