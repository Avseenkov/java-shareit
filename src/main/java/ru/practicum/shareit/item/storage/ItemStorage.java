package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {

    long add(Item item);

    void update(Item item);

    Optional<Item> get(Long id);

    Collection<Item> getAll(long id);

    Collection<Item> findItems(String query);
}
