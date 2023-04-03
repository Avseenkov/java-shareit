package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPlainDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Validated
public class BookingController {

    BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)

    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long id, @RequestBody BookingPlainDto bookingPlainDto) {
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
    public List<BookingDto> getAllBookings(
            @RequestHeader("X-Sharer-User-Id") Long id,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getBookings(id, state, from, size);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getAllOwnerBookings(
            @RequestHeader("X-Sharer-User-Id") Long id,
            @RequestParam(defaultValue = "0")  int from,
            @RequestParam(defaultValue = "100")  int size,
            @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getOwnerBookings(id, state, from, size);
    }


}
