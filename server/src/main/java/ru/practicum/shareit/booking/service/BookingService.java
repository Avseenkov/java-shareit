package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPlainDto;

import java.util.List;

public interface BookingService {

    BookingDto create(BookingPlainDto bookingPlainDto, Long userId);

    BookingDto setApprove(Long bookingId, Long userId, boolean approve);

    BookingDto getBooking(Long bookingId, Long userId);

    List<BookingDto> getBookings(Long userId, String query, int from, int  size);

    List<BookingDto> getOwnerBookings(Long userId, String query, int from, int size);
}
