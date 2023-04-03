package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingPlainDto;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

@Data
public class ItemDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private ItemRequest request;

    private BookingPlainDto lastBooking;

    private BookingPlainDto nextBooking;

    private List<CommentDto> comments;

    private Long requestId;
}
