package ru.practicum.shareit.booking.service;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingPlainDto;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingServiceImpIntegration {

    final BookingServiceImp bookingService;

    final EntityManager em;

    User user;

    User booker;

    Item item;

    BookingDto bookingDto;

    Booking booking;

    User userAnother;

    BookingPlainDto bookingPlainDto;

    Long userId;

    Long bookerId;

    Long itemId;

    BookingStorage bookingStorage;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@test.com");
        user.setName("test");

        booker = new User();
        booker.setEmail("booker@test.com");
        booker.setName("booker");

        item = new Item();
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

        em.persist(user);
        userId = user.getId();

        em.persist(item);
        itemId = item.getId();

        em.persist(booker);
        bookerId = booker.getId();

        bookingPlainDto = new BookingPlainDto();
        bookingPlainDto.setStart(LocalDateTime.of(3022, 1, 1, 0, 0));
        bookingPlainDto.setEnd(LocalDateTime.of(3022, 1, 2, 0, 0));
        bookingPlainDto.setItemId(itemId);
        bookingPlainDto.setBookerId(bookerId);
    }

    @Test
    public void createBooking() {
        bookingService.create(bookingPlainDto, bookerId);

        Booking savedBooking = (Booking) em.createQuery("SELECT b from Booking b where b.booker.id = :id")
                .setParameter("id", bookerId)
                .getSingleResult();

        assertThat(savedBooking.getStatus(), equalTo(Status.WAITING));
        assertThat(savedBooking.getStart(), equalTo(bookingPlainDto.getStart()));
        assertThat(savedBooking.getEnd(), equalTo(bookingPlainDto.getEnd()));
    }


    @Test
    void setApprove() {
        booking.setStatus(Status.WAITING);
        em.persist(booking);
        bookingService.setApprove(booking.getId(), userId, true);

        Booking savedBooking = (Booking) em.createQuery("SELECT b from Booking b where b.item.owner.id = :id")
                .setParameter("id", userId)
                .getSingleResult();

        assertThat(savedBooking.getStatus(), equalTo(Status.APPROVED));

    }

    @Test
    void setRejected() {
        booking.setStatus(Status.WAITING);
        em.persist(booking);
        bookingService.setApprove(booking.getId(), userId, false);

        Booking savedBooking = (Booking) em.createQuery("SELECT b from Booking b where b.item.owner.id = :id")
                .setParameter("id", userId)
                .getSingleResult();

        assertThat(savedBooking.getStatus(), equalTo(Status.REJECTED));
    }

    @Test
    void getBooking() {
        em.persist(booking);

        BookingDto bookingDB = bookingService.getBooking(booking.getId(), userId);

        assertThat(bookingDB.getItem().getDescription(), equalTo(booking.getItem().getDescription()));
        assertThat(bookingDB.getStart(), equalTo(booking.getStart()));
        assertThat(bookingDB.getBooker().getName(), equalTo(booking.getBooker().getName()));

    }

    @Test
    void getBookings() {
        em.persist(booking);

        List<BookingDto> source = List.of(
                bookingDto
        );
        List<BookingDto> target = bookingService.getBookings(bookerId, "ALL", 0, 2);

        assertThat(target, hasSize(source.size()));

        for (BookingDto bookingDto1 : source) {
            assertThat(target, hasItem(allOf(
                    hasProperty("start", equalTo(bookingDto1.getStart())),
                    hasProperty("status", equalTo(bookingDto1.getStatus()))
            )));
        }
    }


}
