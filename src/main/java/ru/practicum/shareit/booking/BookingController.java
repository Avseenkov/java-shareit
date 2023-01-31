package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPlainDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.validation.OnCreate;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ErrorResponse;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Validated
public class BookingController {

    BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @Validated(OnCreate.class)
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long id, @RequestBody @Valid BookingPlainDto bookingPlainDto) {
        return bookingService.create(bookingPlainDto, id);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long id, @PathVariable Long bookingId, @RequestParam boolean approved) {
        return bookingService.setApprove(bookingId, id, approved);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long id, @PathVariable Long bookingId) {
        return bookingService.getBooking(bookingId, id);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getAllBookings(@RequestHeader("X-Sharer-User-Id") Long id, @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getBookings(id, state);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getAllOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long id, @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getOwnerBookings(id, state);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(BadRequestException e) {
        return new ErrorResponse(e.getMessage());
    }
}
