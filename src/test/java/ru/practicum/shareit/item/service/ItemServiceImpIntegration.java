package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceImpIntegration {

    final ItemServiceImpl itemService;
    final EntityManager em;
    User user;

    ItemDto itemDto;

    Item item;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@test.com1");
        user.setName("test");

        itemDto = createItemDto("Test thing", "Description of the thing", true);

        item = new Item();
        item.setName("Test thing");
        item.setDescription("Description of the thing");
        item.setAvailable(true);
    }

    @Test
    public void createItem() {
        em.persist(user);
        itemService.createItem(itemDto, user.getId());

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item savedItem = query.setParameter("name", itemDto.getName()).getSingleResult();

        assertThat(savedItem.getId(), notNullValue());
        assertThat(savedItem.getName(), equalTo(itemDto.getName()));
        assertThat(savedItem.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(savedItem.getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    public void updateItem() {
        em.persist(user);
        item.setOwner(user);
        em.persist(item);
        ItemDto newItem = createItemDto("new name", "new description", false);

        itemService.updateItem(newItem, item.getId(), user.getId());

        TypedQuery<Item> query = em.createQuery("SELECT i from Item i where i.name = :name", Item.class);

        Item savedItem = query.setParameter("name", newItem.getName()).getSingleResult();

        assertThat(savedItem.getName(), equalTo(newItem.getName()));
        assertThat(savedItem.getDescription(), equalTo(newItem.getDescription()));
        assertThat(savedItem.getAvailable(), equalTo(newItem.getAvailable()));

    }

    @Test
    public void getAllItems() {

        em.persist(user);
        List<ItemDto> sourceItems = List.of(
                itemDto,
                createItemDto("item2", "description 2", true),
                createItemDto("item3", "description 3", false)
        );

        for (ItemDto itemDto : sourceItems) {
            Item item = ItemMapper.itemFromItemDto(itemDto);
            item.setOwner(user);
            em.persist(item);
        }
        em.flush();

        List<ItemDto> targetItems = itemService.getAllItems(user.getId(), 0, 3);

        assertThat(targetItems, hasSize(sourceItems.size()));

        for (ItemDto itemDto1 : sourceItems) {
            assertThat(targetItems, hasItem(allOf(
                    hasProperty("name", equalTo(itemDto1.getName())),
                    hasProperty("description", equalTo(itemDto1.getDescription()))
            )));
        }
    }

    @Test
    public void createComment() {
        em.persist(user);
        item.setOwner(user);
        em.persist(item);

        User booker = new User();
        booker.setEmail("booker@test.com");
        booker.setName("booker");

        em.persist(booker);

        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(LocalDateTime.of(2022, 1, 1, 0, 0));
        booking.setEnd(LocalDateTime.of(2022, 1, 1, 10, 0));

        em.persist(booking);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("comment");
        commentDto.setAuthorName("author");

        itemService.createComment(booker.getId(), item.getId(), commentDto);

        TypedQuery<Comment> query = em.createQuery("SELECT c from Comment c where c.author = :author", Comment.class);

        Comment savedComment = query.setParameter("author", booker).getSingleResult();

        assertThat(savedComment.getText(), equalTo(commentDto.getText()));
        assertThat(savedComment.getItem().getName(), equalTo(item.getName()));


    }

    private ItemDto createItemDto(String name, String description, boolean available) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(available);

        return itemDto;
    }
}
