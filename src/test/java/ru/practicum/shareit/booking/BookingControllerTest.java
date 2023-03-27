package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPlainDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @MockBean
    BookingService bookingService;

    BookingPlainDto bookingPlainDto;

    BookingDto bookingDto;

    @Autowired
    MockMvc mvc;

    ObjectMapper mapper = new ObjectMapper();

    static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void setUp() {


        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(new LocalDateTimeSerializer(dateTimeFormatter));

        mapper.registerModule(simpleModule);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("item");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@test.com");
        userDto.setName("test");

        bookingPlainDto = new BookingPlainDto();
        bookingPlainDto.setStart(LocalDateTime.of(3022, 1, 1, 0, 0));
        bookingPlainDto.setEnd(LocalDateTime.of(3022, 1, 2, 0, 0));
        bookingPlainDto.setItemId(1L);
        bookingPlainDto.setBookerId(1L);

        bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.of(3022, 1, 1, 0, 0));
        bookingDto.setEnd(LocalDateTime.of(3022, 1, 2, 0, 0));
        bookingDto.setItem(itemDto);
        bookingDto.setBooker(userDto);
        bookingDto.setStatus(Status.APPROVED);

    }

    @Test
    void create() throws Exception {
        Mockito.when(bookingService.create(Mockito.any(BookingPlainDto.class), Mockito.anyLong()))
                .thenReturn(bookingDto);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingPlainDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));
    }

    @Test
    void approveBooking() throws Exception {
        Mockito.when(bookingService.setApprove(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean()))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}/", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));


    }

    @Test
    void getBooking() throws Exception {
        Mockito.when(bookingService.getBooking(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}/", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));
    }

    @Test
    void getAllBookings() throws Exception {
        Mockito.when(bookingService.getBookings(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));
    }

    @Test
    void getAllBookingsWithWrongFrom() throws Exception {
        Mockito.when(bookingService.getBookings(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllOwnerBookings() throws Exception {
        Mockito.when(bookingService.getOwnerBookings(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));
    }
}