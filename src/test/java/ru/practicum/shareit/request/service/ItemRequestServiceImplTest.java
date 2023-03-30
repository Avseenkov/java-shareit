package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    @Mock
    UserStorage userStorage;

    @Mock
    ItemRequestStorage itemRequestStorage;

    ItemRequest itemRequest;

    User user;

    ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setName("Test");
        user.setEmail("test@test.com");

        itemRequest = new ItemRequest();
        itemRequest.setCreated(LocalDateTime.of(2023, 1, 1, 0, 0, 0));
        itemRequest.setDescription("Description");
        itemRequest.setRequestor(user);

        itemRequestDto = ItemRequestMapper.itemRequestDtoFromItemRequest(itemRequest);

    }

    @Test
    public void createItemRequest() {

        Mockito.when(userStorage.getUserFromStorage(Mockito.anyLong())).thenReturn(user);
        Mockito.when(itemRequestStorage.save(Mockito.any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto itemRequestDtoResult = itemRequestService.createItemRequest(itemRequestDto, 1L);

        assertThat(itemRequestDtoResult.getDescription(), equalTo(itemRequestDto.getDescription()));

        Mockito.verify(userStorage, Mockito.times(1)).getUserFromStorage(Mockito.anyLong());
        Mockito.verify(itemRequestStorage, Mockito.times(1)).save(Mockito.any(ItemRequest.class));
    }

    @Test
    public void getUserAllRequests() {
        Mockito.when(userStorage.getUserFromStorage(Mockito.anyLong())).thenReturn(user);
        Mockito.when(itemRequestStorage.findByUserAll(Mockito.anyLong())).thenReturn(List.of(itemRequest));

        itemRequestService.getUserAllRequests(1L);

        Mockito.verify(userStorage, Mockito.times(1)).getUserFromStorage(Mockito.anyLong());
        Mockito.verify(itemRequestStorage, Mockito.times(1)).findByUserAll(Mockito.anyLong());
    }

    @Test
    public void getAllForeignRequests() {
        Mockito.when(userStorage.getUserFromStorage(Mockito.anyLong())).thenReturn(user);

        Page<ItemRequest> itemsRequestPageable = new PageImpl<>(List.of(itemRequest), PageRequest.of(2, 2), 10);

        Mockito.when(itemRequestStorage.findAllForeignPageable(Mockito.anyLong(), Mockito.any(Pageable.class))).thenReturn(itemsRequestPageable);

        itemRequestService.getAllForeignRequests(1L, 5, 2);

        Mockito.verify(itemRequestStorage, Mockito.times(1))
                .findAllForeignPageable(1L, PageRequest.of(2, 2));

    }

}