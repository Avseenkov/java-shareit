package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

public interface ItemStorageCustom {
    Item getItemFromStorage(Long id);

    Item getItemWithCommentsFromStorage(Long id);
}
