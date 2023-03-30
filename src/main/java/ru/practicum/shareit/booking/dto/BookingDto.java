package ru.practicum.shareit.booking.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.validation.OnCreate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Future;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDto {

    Long id;

    @Future(groups = OnCreate.class)
    LocalDateTime start;

    @Future(groups = OnCreate.class)
    LocalDateTime end;

    ItemDto item;

    UserDto booker;

    Status status;
}
