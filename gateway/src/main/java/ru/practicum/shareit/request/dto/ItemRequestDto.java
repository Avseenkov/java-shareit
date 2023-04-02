package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class ItemRequestDto {

    private Long id;

    @NotBlank
    @NotNull
    private String description;

    private LocalDateTime created;

    private List<ItemForItemRequestDto> items;
}