package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exception.EmailExistException;

import java.util.*;

@Component
public class MemoryStorage implements UserStorage {
    Map<Long, User> users;

    long ids;

    public MemoryStorage() {
        users = new HashMap<>();
        ids = 0L;
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    @Override
    public long add(User user) {
        if (user.getId() == null) user.setId(++ids);
        users.put(user.getId(), user);
        return user.getId();
    }

    @Override
    public void update(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public Optional<User> get(Long id) {
        return users.containsKey(id) ? Optional.of(users.get(id)) : Optional.empty();
    }

    @Override
    public Collection<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void emailIsExist(String email) {
        if (users.values().stream().anyMatch(user -> user.getEmail().equals(email))) {
            throw new EmailExistException(String.format("%s is already exist", email));
        }
    }
}
