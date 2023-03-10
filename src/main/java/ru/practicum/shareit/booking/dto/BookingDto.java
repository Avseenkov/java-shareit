package ru.practicum.shareit.booking.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.validation.OnCreate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.Future;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class BookingDto {

    Long id;

    @Future(groups = OnCreate.class)
    LocalDateTime start;

    @Future(groups = OnCreate.class)
    LocalDateTime end;

    Item item;

    User booker;

    Status status;
}
