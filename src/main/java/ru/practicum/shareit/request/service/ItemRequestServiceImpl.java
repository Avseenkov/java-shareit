package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestStorage itemRequestStorage;
    private final UserStorage userStorage;

    @Override
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long userId) {

        User user = userStorage.getUserFromStorage(userId);

        ItemRequest itemRequest = ItemRequestMapper.itemRequestFromItemRequestDto(itemRequestDto);
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());

        return ItemRequestMapper.itemRequestDtoFromItemRequest(itemRequestStorage.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getUserAllRequests(Long userId) {

        User user = userStorage.getUserFromStorage(userId);

        List<ItemRequest> requests = itemRequestStorage.findByUserAll(userId);

        return requests.stream().map(ItemRequestMapper::itemRequestDtoFromItemRequest).collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllForeignRequests(Long userId, int from, int size) {
        User user = userStorage.getUserFromStorage(userId);

        int page = from / size;

        Page<ItemRequest> requests = itemRequestStorage.findAllForeignPageable(
                userId,
                PageRequest.of(page, size)
        );

        return requests.stream().map(ItemRequestMapper::itemRequestDtoFromItemRequest).collect(Collectors.toList());

    }

    @Override
    public ItemRequestDto getRequest(Long userId, Long itemRequestId) {
        User user = userStorage.getUserFromStorage(userId);
        ItemRequest itemRequest = itemRequestStorage.getItemRequestFromStorage(itemRequestId);
        return ItemRequestMapper.itemRequestDtoFromItemRequest(itemRequest);
    }

}
