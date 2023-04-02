package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {
    Long id;
    String name;
    String email;
}

