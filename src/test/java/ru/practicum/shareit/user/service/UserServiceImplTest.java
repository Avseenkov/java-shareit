package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.EmailExistException;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;
    @Mock
    UserStorage mockUserStorage;

    User user;
    UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setName("test");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@test.com");
        userDto.setName("test");
    }

    @AfterEach
    void end() {
        Mockito.verifyNoMoreInteractions(mockUserStorage);

    }

    @Test
    void createUser() {

        Mockito.when(mockUserStorage.save(Mockito.any(User.class)))
                .thenReturn(user);

        Mockito.when(mockUserStorage.getUserFromStorage(Mockito.anyLong()))
                .thenReturn(user);

        UserDto returnedUser = userService.createUser(userDto);
        assertThat(returnedUser, equalTo(userDto));

        Mockito.verify(mockUserStorage, Mockito.times(1)).save(Mockito.any(User.class));
        Mockito.verify(mockUserStorage, Mockito.times(1)).getUserFromStorage(user.getId());

    }

    @Test
    void getNotExistUser() {
        Mockito.when(mockUserStorage.getUserFromStorage(Mockito.anyLong()))
                .thenThrow(new NotFoundException("User with id = 1 not found"));

        assertThrows(NotFoundException.class, () -> userService.getUser(1L));
        Mockito.verify(mockUserStorage, Mockito.times(1)).getUserFromStorage(user.getId());
    }

    @Test
    void updateUser() {
        String newEmail = "new_email";
        String newName = "new_name";

        UserDto newUserDto = new UserDto();

        newUserDto.setId(1L);
        newUserDto.setEmail(newEmail);
        newUserDto.setName(newName);


        User newUser = new User();
        newUser.setId(1L);
        newUser.setName(newName);
        newUser.setEmail(newEmail);

        Mockito.when(mockUserStorage.getUserFromStorage(Mockito.anyLong()))
                .thenReturn(newUser);
        Mockito.when(mockUserStorage.save(Mockito.any(User.class)))
                .thenReturn(newUser);
        Mockito.when(mockUserStorage.findByEmailIgnoreCaseAndIdNot(Mockito.anyString(), Mockito.anyLong()))
                .thenReturn(Optional.empty());
        UserDto afterUpdate = userService.updateUser(1L, newUserDto);

        assertThat(afterUpdate, equalTo(newUserDto));

        Mockito.verify(mockUserStorage, Mockito.times(1)).save(newUser);

    }

    @Test
    void deleteUser() {
        Mockito.when(mockUserStorage.getReferenceById(Mockito.anyLong()))
                .thenReturn(user);
        Mockito.doNothing().when(mockUserStorage).delete(Mockito.any(User.class));

        userService.deleteUser(1L);

        Mockito.verify(mockUserStorage, Mockito.times(1)).getReferenceById(1L);
        Mockito.verify(mockUserStorage, Mockito.times(1)).delete(user);
    }

    @Test
    void emailIsExistWithNewEmail() {
        Mockito.when(mockUserStorage.findByEmailIgnoreCaseAndIdNot(Mockito.anyString(), Mockito.anyLong()))
                .thenReturn(Optional.empty());

        userService.emailIsExist(user.getEmail(), user.getId());

        Mockito.verify(mockUserStorage, Mockito.times(1)).findByEmailIgnoreCaseAndIdNot(user.getEmail(), user.getId());
    }

    @Test
    void emailIsExistWithExistEmail() {
        Mockito.when(mockUserStorage.findByEmailIgnoreCaseAndIdNot(Mockito.anyString(), Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        assertThrows(EmailExistException.class, () -> userService.emailIsExist(user.getEmail(), user.getId()));

        Mockito.verify(mockUserStorage, Mockito.times(1)).findByEmailIgnoreCaseAndIdNot(user.getEmail(), user.getId());
    }

    @Test
    void getAllUsers() {

        User user1 = new User();
        user1.setId(2L);
        user1.setEmail("test1@test.com");
        user1.setName("test1");

        Mockito
                .when(mockUserStorage.findAll())
                .thenReturn(List.of(user, user1));
        List<UserDto> usersResult = userService.getAllUsers();
        assertEquals(usersResult.size(), 2);

    }


}