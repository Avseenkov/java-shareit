package ru.practicum.shareit.request.storage;

import ru.practicum.shareit.request.ItemRequest;

public interface ItemRequestCustom {
    ItemRequest getItemRequestFromStorage(long id);
}
