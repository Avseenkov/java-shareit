package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

public interface UserStorageCustom {
    User getUserFromStorage(Long id);
}
