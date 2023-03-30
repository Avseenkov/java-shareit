package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDto> getUserAllRequests(Long userId);

    List<ItemRequestDto> getAllForeignRequests(Long userId, int from, int size);

    ItemRequestDto getRequest(Long userId, Long itemRequestId);
}
