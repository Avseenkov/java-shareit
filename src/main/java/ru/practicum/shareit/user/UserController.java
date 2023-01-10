package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {

    UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody @Valid UserDto user) {
        return userService.createUser(user);
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUser(@RequestBody UserDto user, @PathVariable Long id) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }
}
