package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingPlainDto {

    Long id;

    LocalDateTime start;

    LocalDateTime end;

    Long itemId;

    Long bookerId;

    Status status;
}
