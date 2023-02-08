package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;

import java.util.ArrayList;
import java.util.List;

public class BookingMapper {
    public static Booking bookingFromBookingDto(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(bookingDto.getItem());
        booking.setBooker(bookingDto.getBooker());
        booking.setStatus(bookingDto.getStatus());
        return booking;
    }

    public static BookingDto bookingDtoFromBooking(Booking booking) {
        return BookingDto
                .builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .id(booking.getId())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    public static Booking bookingFromBookingPlainDto(BookingPlainDto bookingPlainDto) {
        Booking booking = new Booking();
        booking.setId(bookingPlainDto.getId());
        booking.setStart(bookingPlainDto.getStart());
        booking.setEnd(bookingPlainDto.getEnd());
        return booking;
    }

    public static BookingPlainDto bookingPlainDtoFromBooking(Booking booking) {
        return BookingPlainDto
                .builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .id(booking.getId())
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .status(booking.getStatus())
                .build();
    }

    public static List<BookingDto> mapToBookingDto(Iterable<Booking> bookings) {
        List<BookingDto> dtos = new ArrayList<>();
        for (Booking booking : bookings) {
            dtos.add(bookingDtoFromBooking(booking));
        }
        return dtos;
    }
}


