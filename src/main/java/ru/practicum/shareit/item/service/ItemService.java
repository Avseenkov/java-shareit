package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, long itemId, long userId);

    ItemDto getItem(long userId, long itemId);

    List<ItemDto> findItems(long userId, String query, int from, int size);

    List<ItemDto> getAllItems(long id, int from, int size);

    CommentDto createComment(long userId, long itemId, CommentDto commentDto);
}
