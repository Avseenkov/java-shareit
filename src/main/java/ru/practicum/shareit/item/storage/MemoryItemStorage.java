package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MemoryItemStorage implements ItemStorage {

    Map<Long, Item> items;
    long ids;

    public MemoryItemStorage() {
        items = new HashMap<>();
        ids = 0L;
    }

    @Override
    public long add(Item item) {
        if (item.getId() == null) {
            item.setId(++ids);
        }
        items.put(item.getId(), item);
        return item.getId();
    }

    @Override
    public void update(Item item) {
        items.put(item.getId(), item);
    }

    @Override
    public Collection<Item> getAll(long id) {
        return items.values().stream().filter(item -> item.getOwner().getId() == id).collect(Collectors.toList());
    }

    @Override
    public Optional<Item> get(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Collection<Item> findItems(String query) {
        return items.values().stream()
                .filter(item ->
                        item.getAvailable()
                                && (item.getName().toLowerCase().contains(query.toLowerCase())
                                || item.getDescription().toLowerCase().contains(query.toLowerCase()))
                )
                .collect(Collectors.toList());
    }
}
