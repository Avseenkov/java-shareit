package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    Long id;
    @NotBlank(message = "Name should not be empty")
    String name;
    @NotBlank(message = "Email should not be empty")
    @Email
    String email;
}

