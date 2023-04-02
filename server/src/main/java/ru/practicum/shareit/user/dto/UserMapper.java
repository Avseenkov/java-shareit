package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.User;

public class UserMapper {
    public static User userFromUserDto(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        return user;
    }

    public static UserDto userDtoFromUser(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());

        return userDto;
    }
}
