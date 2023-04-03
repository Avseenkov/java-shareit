package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class UserDto {
    Long id;
    @NotBlank(message = "Name should not be empty")
    String name;
    @NotBlank(message = "Email should not be empty")
    @Email
    String email;
}

