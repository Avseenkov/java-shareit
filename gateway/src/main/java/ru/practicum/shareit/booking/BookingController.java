package ru.practicum.shareit.booking;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingPlainDto;
import ru.practicum.shareit.validation.OnCreate;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@RestController
@AllArgsConstructor
@RequestMapping("bookings")
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @Validated(OnCreate.class)
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") @NotNull
                                         @Min(1) Long id, @RequestBody @Valid BookingPlainDto bookingPlainDto) {
        return bookingClient.createBooking(id, bookingPlainDto);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") @NotNull
                                                 @Min(1) Long id, @PathVariable Long bookingId, @RequestParam boolean approved) {
        return bookingClient.approveBooking(id, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") @NotNull
                                             @Min(1) Long id, @PathVariable Long bookingId) {
        return bookingClient.getBooking(id, bookingId);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllBookings(
            @RequestHeader("X-Sharer-User-Id")
            @NotNull
            @Min(1) Long id,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "100") @Min(1) int size,
            @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getAllBookings(id, from, size, state);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllOwnerBookings(
            @RequestHeader("X-Sharer-User-Id")
            @NotNull
            @Min(1) Long id,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "100") @Min(1) int size,
            @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getAllOwnerBookings(id, from, size, state);
    }
}
