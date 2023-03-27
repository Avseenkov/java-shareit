package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;

import java.util.stream.Collectors;



public class ItemRequestMapper {
    public static ItemRequest itemRequestFromItemRequestDto(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestDto.getId());
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setCreated(itemRequestDto.getCreated());
        return itemRequest;
    }

    public static ItemRequestDto itemRequestDtoFromItemRequest(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemRequest.getItems().stream().map(ItemMapper::itemForItemRequestDtoFromItem).collect(Collectors.toList()))
                .build();
    }
}
