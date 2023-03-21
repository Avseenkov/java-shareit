package ru.practicum.shareit.item.storage;

import org.springframework.context.annotation.Lazy;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;


public class ItemStorageCustomImpl implements ItemStorageCustom {

    private final ItemStorage itemStorage;

    public ItemStorageCustomImpl(@Lazy ItemStorage itemStorage) {
        this.itemStorage = itemStorage;
    }

    @Override
    public Item getItemFromStorage(Long itemId) {
        return itemStorage.findById(itemId).orElseThrow(() -> new NotFoundException(String.format("Item with id = %s not found", itemId)));
    }

    @Override
    public Item getItemWithCommentsFromStorage(Long itemId) {
        return itemStorage.findByIdWithComments(itemId).orElseThrow(() -> new NotFoundException(String.format("Item with id = %s not found", itemId)));
    }
}
