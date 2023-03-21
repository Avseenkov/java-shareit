package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @MockBean
    private UserServiceImpl userService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@test.com");
        userDto.setName("test");
    }

    @Test
    void create() throws Exception {
        Mockito.when(userService.createUser(Mockito.any(UserDto.class)))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

    }

    @Test
    void update() throws Exception {

        UserDto newUserDto = new UserDto();
        newUserDto.setId(1L);
        newUserDto.setEmail("test_update@test.com");
        newUserDto.setName("test_update");
        Mockito.when(userService.updateUser(Mockito.anyLong(), Mockito.any(UserDto.class)))
                .thenReturn(newUserDto);

        mvc.perform(patch("/users/{id}", userDto.getId())
                        .content(mapper.writeValueAsString(newUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(newUserDto.getName())))
                .andExpect(jsonPath("$.email", is(newUserDto.getEmail())));
    }

    @Test
    void updateWithWrongUser() throws Exception {
        Mockito.when(userService.updateUser(Mockito.anyLong(), Mockito.any(UserDto.class)))
                .thenThrow(new NotFoundException("User not found"));

        mvc.perform(patch("/users/{id}", userDto.getId())
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void getUser() throws Exception {
        Mockito.when(userService.getUser(Mockito.anyLong()))
                .thenReturn(userDto);

        mvc.perform(get("/users/{id}", userDto.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

    }

    @Test
    void getWrongUser() throws Exception {
        Mockito.when(userService.getUser(Mockito.anyLong()))
                .thenThrow(new NotFoundException("User not found"));

        mvc.perform(get("/users/{id}", userDto.getId())
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    void deleteUser() throws Exception {
        Mockito.doNothing().when(userService).deleteUser(Mockito.anyLong());
        mvc.perform(delete("/users/{id}", userDto.getId())
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());


    }

    @Test
    void getAllUsers() throws Exception {
        UserDto userDto2 = new UserDto();
        userDto2.setId(2L);
        userDto2.setEmail("test2@test.com");
        userDto2.setName("test2");

        Mockito.when(userService.getAllUsers())
                .thenReturn(List.of(userDto, userDto2));

        mvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$.[0].email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.[1].id", is(2)))
                .andExpect(jsonPath("$.[1].name", is(userDto2.getName())))
                .andExpect(jsonPath("$.[1].email", is(userDto2.getEmail())));
    }
}