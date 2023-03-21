package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.validation.OnCreate;

import javax.validation.constraints.Future;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingPlainDto {

    Long id;

    @Future(groups = OnCreate.class)
    LocalDateTime start;

    @Future(groups = OnCreate.class)
    LocalDateTime end;

    Long itemId;

    Long bookerId;

    Status status;
}
