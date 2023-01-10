package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class User {
    Long id;
    @NotBlank(message = "Name should not be empty")
    String name;
    @NotBlank(message = "Email should not be empty")
    @Email
    String email;
}
