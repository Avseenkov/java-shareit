package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("users")
@AllArgsConstructor
public class UserController {

    private final UserClient userClient;

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getUser(@NotNull @Min(1) @PathVariable Long id) {
        return userClient.getUser(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestBody @Valid UserDto user) {
        return userClient.createUser(user);
    }

    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateUser(@RequestBody UserDto user, @NotNull @Min(1) @PathVariable Long id) {
        return userClient.updateUser(user, id);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable @NotNull @Min(1) Long id) {
        userClient.deleteUser(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllUsers() {
        return userClient.getAllUsers();
    }
}
