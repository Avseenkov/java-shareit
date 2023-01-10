package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    long add(User user);

    void update(User user);

    Optional<User> get(Long id);

    Collection<User> getAll();

    void delete(Long id);

    void emailIsExist(String email);
}
