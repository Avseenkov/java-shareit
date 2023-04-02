package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @MockBean
    ItemRequestServiceImpl itemRequestService;

    final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    MockMvc mvc;

    User user;

    ItemRequest itemRequest;

    ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {

        mapper.registerModule(new JavaTimeModule());
        user = new User();
        user.setName("Test");
        user.setEmail("test@test.com");

        itemRequest = new ItemRequest();
        itemRequest.setCreated(LocalDateTime.of(2023, 1, 1, 0, 0, 0));
        itemRequest.setDescription("Description");
        itemRequest.setRequestor(user);

        itemRequestDto = ItemRequestMapper.itemRequestDtoFromItemRequest(itemRequest);
        itemRequestDto.setId(1L);

    }

    @Test
    void create() throws Exception {

        Mockito.when(itemRequestService.createItemRequest(Mockito.any(ItemRequestDto.class), Mockito.anyLong()))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemRequestDto))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));

    }

    @Test
    void getUserAllRequests() throws Exception {
        Mockito.when(itemRequestService.getUserAllRequests(Mockito.anyLong()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].description", is(itemRequestDto.getDescription())));
    }

    @Test
    void getAllForeignRequests() throws Exception {
        Mockito.when(itemRequestService.getAllForeignRequests(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].description", is(itemRequestDto.getDescription())));
    }

    @Test
    void getRequest() throws Exception {
        Mockito.when(itemRequestService.getRequest(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemRequestDto);

        mvc.perform(get("/requests/{requestId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }
}