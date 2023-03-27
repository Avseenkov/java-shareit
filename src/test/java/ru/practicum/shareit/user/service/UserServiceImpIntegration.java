package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpIntegration {

    final UserServiceImpl userService;
    final EntityManager em;
    User user;
    UserDto userDto;
    @Autowired
    private UserStorage userStorage;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@test.com1");
        user.setName("test");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@test.com");
        userDto.setName("test");
    }

    @Test
    public void save() {

        userService.createUser(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);

        User userDb = query.setParameter("email", userDto.getEmail()).getSingleResult();

        assertThat(userDb.getId(), notNullValue());
        assertThat(userDb.getName(), equalTo(userDto.getName()));
        assertThat(userDb.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    public void updateUser() {

        UserDto savedUser = userService.createUser(userDto);

        UserDto newUser = new UserDto();
        newUser.setId(savedUser.getId());
        newUser.setEmail("new_test@test.ru");
        newUser.setName("new name");

        userService.updateUser(savedUser.getId(), newUser);

        TypedQuery<User> query = em.createQuery("SELECT u FROM User u where u.email = :email", User.class);

        User userDb = query.setParameter("email", newUser.getEmail()).getSingleResult();

        assertThat(userDb.getName(), equalTo(newUser.getName()));
        assertThat(userDb.getEmail(), equalTo(newUser.getEmail()));

    }

    @Test
    public void getUser() {
        em.persist(user);
        UserDto userDto1 = userService.getUser(user.getId());

        assertThat(userDto1.getName(), equalTo(user.getName()));
        assertThat(userDto1.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    public void deleteUser() {
        em.persist(user);
        userService.deleteUser(user.getId());
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u where u.id = :id", User.class);
        List usersDb = query.setParameter("id", user.getId()).getResultList();
        assertThat(usersDb.size(), equalTo(0));
    }
}
