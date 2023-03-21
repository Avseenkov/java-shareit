package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(properties = "db.name=test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestServiceIntegration {

    private final ItemRequestServiceImpl itemRequestService;

    private User user;

    private ItemRequest itemRequest;

    private ItemRequestDto itemRequestDto;

    private final EntityManager em;

    private Long userId;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setName("Test");
        user.setEmail("test@test.com");

        itemRequest = new ItemRequest();
        itemRequest.setCreated(LocalDateTime.of(2023, 1, 1, 0, 0, 0));
        itemRequest.setDescription("Description");
        itemRequest.setRequestor(user);

        itemRequestDto = ItemRequestMapper.itemRequestDtoFromItemRequest(itemRequest);
        em.persist(user);
        userId = user.getId();
    }

    @Test
    public void createItemRequest() {

        itemRequestService.createItemRequest(itemRequestDto, userId);

        TypedQuery<ItemRequest> query = em.createQuery("Select i from ItemRequest i where i.requestor.id = :id", ItemRequest.class);
        ItemRequest savedItemRequest = query.setParameter("id", userId).getSingleResult();

        assertThat(savedItemRequest.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(savedItemRequest.getRequestor().getId(), equalTo(userId));

    }

    @Test
    public void getAllItemRequest() {
        itemRequestService.createItemRequest(itemRequestDto, userId);
        List itemsRequest = em.createQuery("SELECT i from  ItemRequest i WHERE i.requestor.id = :id")
                .setParameter("id", userId)
                .getResultList();
        assertThat(1, equalTo(itemsRequest.size()));

    }

    @Test
    public void getForeignRequests() {
        itemRequestService.createItemRequest(itemRequestDto, userId);
        List itemsRequest = em.createQuery("SELECT i from  ItemRequest i WHERE i.requestor.id <> :id")
                .setParameter("id", userId)
                .getResultList();
        assertThat(0, equalTo(itemsRequest.size()));
    }
}
