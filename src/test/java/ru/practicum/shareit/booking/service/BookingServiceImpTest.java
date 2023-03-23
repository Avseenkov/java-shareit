package ru.practicum.shareit.booking.service;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingPlainDto;
import ru.practicum.shareit.booking.QBooking;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class BookingServiceImpTest {

    @InjectMocks
    private BookingServiceImp bookingService;

    @Mock
    private UserStorage userStorage;

    @Mock
    private ItemStorage itemStorage;

    @Mock
    private BookingStorage bookingStorage;

    private User user;

    private User booker;

    private Item item;

    private BookingDto bookingDto;

    private Booking booking;

    private User userAnother;

    private BookingPlainDto bookingPlainDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setName("test");


        userAnother = new User();
        userAnother.setId(3L);
        userAnother.setEmail("test@test.com");
        userAnother.setName("test");


        booker = new User();
        booker.setId(2L);
        booker.setEmail("booker@test.com");
        booker.setName("booker");

        item = new Item();
        item.setId(1L);
        item.setName("Test thing");
        item.setAvailable(true);
        item.setOwner(user);
        item.setDescription("Description of the thing");

        bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.of(3022, 1, 1, 0, 0));
        bookingDto.setEnd(LocalDateTime.of(3022, 1, 2, 0, 0));
        bookingDto.setItem(ItemMapper.itemDtoFromItem(item));
        bookingDto.setStatus(Status.APPROVED);

        booking = BookingMapper.bookingFromBookingDto(bookingDto);
        booking.setItem(item);
        booking.setBooker(booker);

        bookingPlainDto = new BookingPlainDto();
        bookingPlainDto.setStart(LocalDateTime.of(3022, 1, 1, 0, 0));
        bookingPlainDto.setEnd(LocalDateTime.of(3022, 1, 2, 0, 0));
        bookingPlainDto.setItemId(1L);
        bookingPlainDto.setBookerId(1L);

    }

    @Test
    void createBookingForOwnItem() {
        Mockito.when(userStorage.getUserFromStorage(Mockito.anyLong()))
                .thenReturn(user);

        Mockito.when(itemStorage.getItemFromStorage(Mockito.anyLong()))
                .thenReturn(item);

        Exception exception = assertThrows(NotFoundException.class, () -> bookingService.create(bookingPlainDto, 1L));
        assertTrue(exception.getMessage().contains("It is prohibited to create booking for your item"));
    }

    @Test
    void createBookingForNotAvailableItem() {
        Mockito.when(userStorage.getUserFromStorage(Mockito.anyLong()))
                .thenReturn(booker);

        Mockito.when(itemStorage.getItemFromStorage(Mockito.anyLong()))
                .thenReturn(item);

        item.setAvailable(false);

        assertThrows(BadRequestException.class, () -> bookingService.create(bookingPlainDto, 1L));
    }

    @Test
    void createBookingWhenEndDateIsBeforeStart() {
        Mockito.when(userStorage.getUserFromStorage(Mockito.anyLong()))
                .thenReturn(booker);

        Mockito.when(itemStorage.getItemFromStorage(Mockito.anyLong()))
                .thenReturn(item);

        bookingPlainDto.setEnd(LocalDateTime.of(1, 1, 1, 0, 0));

        assertThrows(BadRequestException.class, () -> bookingService.create(bookingPlainDto, 1L));
    }

    @Test
    void createBooking() {
        Mockito.when(userStorage.getUserFromStorage(Mockito.anyLong()))
                .thenReturn(booker);

        Mockito.when(itemStorage.getItemFromStorage(Mockito.anyLong()))
                .thenReturn(item);

        Mockito.when(bookingStorage.save(Mockito.any(Booking.class)))
                .thenReturn(booking);

        booking.setStatus(Status.WAITING);

        BookingDto bookingDtoDB = bookingService.create(bookingPlainDto, 1L);

        assertThat(bookingDtoDB.getStatus(), equalTo(Status.WAITING));
        assertThat(bookingDtoDB.getBooker().getName(), equalTo(booker.getName()));
    }

    @Test
    void setApproveForApprovedItem() {

        Mockito.when(bookingStorage.getBookingFromStorage(Mockito.anyLong()))
                .thenReturn(booking);
        booking.setStatus(Status.APPROVED);

        assertThrows(BadRequestException.class, () -> bookingService.setApprove(1L, user.getId(), true));
    }

    @Test
    void setApproveWrongOwner() {

        Mockito.when(bookingStorage.getBookingFromStorage(Mockito.anyLong()))
                .thenReturn(booking);
        booking.setStatus(Status.WAITING);
        booking.getItem().setOwner(booker);
        assertThrows(NotFoundException.class, () -> bookingService.setApprove(1L, user.getId(), true));
    }

    @Test
    void setApprove() {
        Mockito.when(bookingStorage.getBookingFromStorage(Mockito.anyLong()))
                .thenReturn(booking);
        booking.setStatus(Status.WAITING);

        Mockito.when(bookingStorage.save(Mockito.any(Booking.class)))
                .thenReturn(booking);

        BookingDto bookingDtoDB = bookingService.setApprove(1L, user.getId(), true);
        assertThat(bookingDto.getStatus(), equalTo(Status.APPROVED));
    }

    @Test
    void getBookingNotOwnAndNotOwnItem() {

        Mockito.when(userStorage.getUserFromStorage(Mockito.anyLong()))
                .thenReturn(userAnother);

        Mockito.when(bookingStorage.getBookingFromStorage(Mockito.anyLong()))
                .thenReturn(booking);

        assertThrows(NotFoundException.class, () -> bookingService.getBooking(1L, 1L));
    }

    @Test
    void getBookings() {
        Mockito.when(userStorage.getUserFromStorage(Mockito.anyLong()))
                .thenReturn(user);

        Page<Booking> itemsRequestPageable = new PageImpl<>(List.of(booking), PageRequest.of(2, 2), 10);


        Mockito.when(bookingStorage.findAll(Mockito.any(Predicate.class), Mockito.any(Pageable.class))).thenReturn(itemsRequestPageable);

        bookingService.getBookings(1L, "ALL", 5, 2);

        BooleanExpression byBookerId = QBooking.booking.booker.id.eq(user.getId());
        Sort sortBy = Sort.by(Sort.Direction.DESC, "start");
        Mockito.verify(bookingStorage, Mockito.times(1))
                .findAll(byBookerId, PageRequest.of(2, 2, sortBy));
    }

    @Test
    void getOwnerBookings() {

        Mockito.when(userStorage.getUserFromStorage(Mockito.anyLong()))
                .thenReturn(user);

        Page<Booking> itemsRequestPageable = new PageImpl<>(List.of(booking), PageRequest.of(2, 2), 10);


        Mockito.when(bookingStorage.findAll(Mockito.any(Predicate.class), Mockito.any(Pageable.class))).thenReturn(itemsRequestPageable);

        bookingService.getOwnerBookings(1L, "ALL", 5, 2);

        BooleanExpression byItemOwnerId = QBooking.booking.item.owner.id.eq(user.getId());
        Sort sortBy = Sort.by(Sort.Direction.DESC, "start");
        Mockito.verify(bookingStorage, Mockito.times(1))
                .findAll(byItemOwnerId, PageRequest.of(2, 2, sortBy));
    }

    @Test
    void getOwnerBookingsWAITING() {

        Mockito.when(userStorage.getUserFromStorage(Mockito.anyLong()))
                .thenReturn(user);

        Page<Booking> itemsRequestPageable = new PageImpl<>(List.of(booking), PageRequest.of(2, 2), 10);


        Mockito.when(bookingStorage.findAll(Mockito.any(Predicate.class), Mockito.any(Pageable.class))).thenReturn(itemsRequestPageable);

        bookingService.getOwnerBookings(1L, "WAITING", 5, 2);

        BooleanExpression byItemOwnerId = QBooking.booking.item.owner.id.eq(user.getId());
        Sort sortBy = Sort.by(Sort.Direction.DESC, "start");
        Mockito.verify(bookingStorage, Mockito.times(1))
                .findAll(byItemOwnerId.and(QBooking.booking.status.eq(Status.WAITING)), PageRequest.of(2, 2, sortBy));
    }

    @Test
    void getOwnerBookingsREJECTED() {

        Mockito.when(userStorage.getUserFromStorage(Mockito.anyLong()))
                .thenReturn(user);

        Page<Booking> itemsRequestPageable = new PageImpl<>(List.of(booking), PageRequest.of(2, 2), 10);

        Mockito.when(bookingStorage.findAll(Mockito.any(Predicate.class), Mockito.any(Pageable.class))).thenReturn(itemsRequestPageable);

        bookingService.getOwnerBookings(1L, "REJECTED", 5, 2);

        BooleanExpression byItemOwnerId = QBooking.booking.item.owner.id.eq(user.getId());
        Sort sortBy = Sort.by(Sort.Direction.DESC, "start");
        Mockito.verify(bookingStorage, Mockito.times(1))
                .findAll(byItemOwnerId.and(QBooking.booking.status.eq(Status.REJECTED)), PageRequest.of(2, 2, sortBy));
    }
}
