package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemService {

    UserService userService;
    ItemStorage itemStorage;

    public ItemDto createItem(ItemDto itemDto, long userId) {
        Item item = ItemMapper.ItemFromItemDto(itemDto);
        UserDto user = userService.getUser(userId);
        item.setOwner(UserMapper.UserFromUserDto(user));
        return getItem(itemStorage.add(item));
    }

    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        Item item = getStorageItem(itemId);
        User owner = item.getOwner();

        if (owner.getId() != userId) {
            throw new NotFoundException("Item not found");
        }

        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());

        itemStorage.update(item);

        return ItemMapper.ItemDtoFromItem(item);
    }

    public ItemDto getItem(long itemId) {
        Item item = getStorageItem(itemId);
        return ItemMapper.ItemDtoFromItem(item);
    }

    public List<ItemDto> findItems(String query) {
        if (query.isBlank() || query.isEmpty()) return List.of();
        return itemStorage.findItems(query).stream().map(ItemMapper::ItemDtoFromItem).collect(Collectors.toList());
    }

    public List<ItemDto> getAllItems(long id) {
        return itemStorage.getAll(id).stream().map(ItemMapper::ItemDtoFromItem).collect(Collectors.toList());
    }

    private Item getStorageItem(long itemId) {
        return itemStorage.get(itemId).orElseThrow(() -> new NotFoundException(String.format("Item with id = %s not found", itemId)));
    }
}
