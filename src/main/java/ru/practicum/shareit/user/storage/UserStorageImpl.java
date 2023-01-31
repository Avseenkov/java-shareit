package ru.practicum.shareit.user.storage;

import org.springframework.context.annotation.Lazy;
import ru.practicum.shareit.user.exception.EmailExistException;


public class UserStorageImpl implements UserStorageCustom {

    private final UserStorage userStorage;

    public UserStorageImpl(@Lazy UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public void emailIsExist(String email) {
        userStorage.findByEmailIgnoreCase(email).ifPresent(user -> {
            throw new EmailExistException(String.format("%s is already exist", email));
        });
    }
}
