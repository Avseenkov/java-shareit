package ru.practicum.shareit.booking.storage;

import org.springframework.context.annotation.Lazy;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.exception.NotFoundException;

public class BookingStorageCustomImpl implements BookingStorageCustom {

    private final BookingStorage bookingStorage;

    public BookingStorageCustomImpl(@Lazy BookingStorage bookingStorage) {
        this.bookingStorage = bookingStorage;
    }

    @Override
    public Booking getBookingFromStorage(long bookingId) {
        return bookingStorage.findById(bookingId).orElseThrow(() -> new NotFoundException(String.format("Booking with id = %s not found", bookingId)));
    }
}
