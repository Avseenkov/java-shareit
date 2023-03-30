package ru.practicum.shareit.request.storage;

import org.springframework.context.annotation.Lazy;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequest;


public class ItemRequestCustomImpl implements ItemRequestCustom {

    private final ItemRequestStorage itemRequestStorage;

    public ItemRequestCustomImpl(@Lazy ItemRequestStorage itemRequestStorage) {
        this.itemRequestStorage = itemRequestStorage;
    }

    @Override
    public ItemRequest getItemRequestFromStorage(long id) {
        return itemRequestStorage.findById(id).orElseThrow(() -> new NotFoundException(String.format("Item request with id = %s not found", id)));

    }
}
