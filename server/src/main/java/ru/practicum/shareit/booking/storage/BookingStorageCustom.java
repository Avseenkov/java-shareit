package ru.practicum.shareit.booking.storage;

import ru.practicum.shareit.booking.Booking;

public interface BookingStorageCustom {
    Booking getBookingFromStorage(long id);
}
