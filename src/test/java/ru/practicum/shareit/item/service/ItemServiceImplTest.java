package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @InjectMocks
    ItemServiceImpl itemService;

    @Mock
    UserStorage mockUserStorage;

    @Mock
    ItemStorage mockItemStorage;

    @Mock
    BookingStorage mockBookingStorage;

    @Mock
    CommentStorage mockCommentStorage;

    @Mock
    ItemRequestStorage mockItemRequestStorage;

    User user;

    User booker;
    Item item;

    ItemDto itemDto;

    Booking lastBooking;

    Booking nextBooking;

    CommentDto commentDto;

    Comment comment;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setName("test");

        booker = new User();
        booker.setId(2L);
        booker.setEmail("booker@test.com");
        booker.setName("booker");

        item = new Item();
        item.setId(1L);
        item.setName("Test thing");
        item.setAvailable(true);
        item.setOwner(user);
        item.setDescription("Description of the thing");

        itemDto = createItemDto("Test thing", "Description of the thing", true);
        lastBooking = new Booking();
        lastBooking.setId(1L);
        lastBooking.setItem(item);
        lastBooking.setStatus(Status.APPROVED);
        lastBooking.setStart(LocalDateTime.of(2023, 1, 1, 0, 0));
        lastBooking.setEnd(LocalDateTime.of(2023, 1, 1, 0, 0));
        lastBooking.setBooker(booker);

        nextBooking = new Booking();
        nextBooking.setId(2L);
        nextBooking.setItem(item);
        nextBooking.setStatus(Status.WAITING);
        nextBooking.setStart(LocalDateTime.of(2053, 10, 1, 0, 0));
        nextBooking.setEnd(LocalDateTime.of(2053, 10, 1, 1, 0));
        nextBooking.setBooker(booker);

        commentDto = new CommentDto();
        commentDto.setText("First comment");
        commentDto.setAuthorName(booker.getName());

        comment = new Comment();
        comment.setId(1L);
        comment.setText("First comment");
        comment.setAuthor(booker);
    }

    @Test
    public void createItem() {
        Mockito.when(mockUserStorage.getUserFromStorage(Mockito.anyLong()))
                .thenReturn(user);
        Mockito.when(mockItemStorage.save(Mockito.any(Item.class)))
                .thenReturn(item);

        ItemDto itemDto1 = itemService.createItem(itemDto, user.getId());

        Mockito.verify(mockUserStorage, Mockito.times(1)).getUserFromStorage(user.getId());
        Mockito.verify(mockItemStorage, Mockito.times(1)).save(Mockito.any(Item.class));

        assertThat(itemDto1.getName(), equalTo(itemDto1.getName()));
        assertThat(itemDto1.getDescription(), equalTo(itemDto1.getDescription()));
        assertThat(itemDto1.getAvailable(), equalTo(itemDto1.getAvailable()));
    }

    @Test
    public void createItemWithRequest() {
        Mockito.when(mockUserStorage.getUserFromStorage(Mockito.anyLong()))
                .thenReturn(user);
        Mockito.when(mockItemStorage.save(Mockito.any(Item.class)))
                .thenReturn(item);

        ItemRequest itemRequest = new ItemRequest();

        Mockito.when(mockItemRequestStorage.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemRequest));

        itemDto.setRequestId(1L);
        ItemDto itemDto1 = itemService.createItem(itemDto, user.getId());

        Mockito.verify(mockUserStorage, Mockito.times(1)).getUserFromStorage(user.getId());
        Mockito.verify(mockItemStorage, Mockito.times(1)).save(Mockito.any(Item.class));
        Mockito.verify(mockItemRequestStorage, Mockito.times(1)).findById(Mockito.anyLong());

        assertThat(itemDto1.getName(), equalTo(itemDto1.getName()));
        assertThat(itemDto1.getDescription(), equalTo(itemDto1.getDescription()));
        assertThat(itemDto1.getAvailable(), equalTo(itemDto1.getAvailable()));
    }

    @Test
    public void createItemNotFoundUser() {
        Mockito.when(mockUserStorage.getUserFromStorage(Mockito.anyLong()))
                .thenThrow(new NotFoundException("user not found"));

        assertThrows(NotFoundException.class, () -> itemService.createItem(itemDto, user.getId()));

    }


    @Test
    void updateItemNotFoundItem() {
        Mockito.when(mockItemStorage.getItemFromStorage(item.getId()))
                .thenThrow(new NotFoundException("item not found"));

        assertThrows(NotFoundException.class, () -> itemService.updateItem(itemDto, item.getId(), user.getId()));
    }

    @Test
    void updateItem() {

        Item mewItem = new Item();
        mewItem.setId(item.getId());
        mewItem.setAvailable(item.getAvailable());
        mewItem.setDescription("new description");
        mewItem.setName("new name");

        Mockito.when(mockItemStorage.getItemFromStorage(item.getId()))
                .thenReturn(item);

        Mockito.when(mockItemStorage.save(Mockito.any(Item.class)))
                .thenReturn(mewItem);

        ItemDto updatedItemDto = itemService.updateItem(itemDto, item.getId(), user.getId());

        assertThat(updatedItemDto.getName(), equalTo(mewItem.getName()));
        assertThat(updatedItemDto.getDescription(), equalTo(mewItem.getDescription()));
        assertThat(updatedItemDto.getAvailable(), equalTo(mewItem.getAvailable()));
    }

    @Test
    void updateItemWithEmptyField() {

        Item mewItem = new Item();
        mewItem.setId(item.getId());
        mewItem.setAvailable(item.getAvailable());
        mewItem.setDescription("new description");
        mewItem.setName("new name");

        Mockito.when(mockItemStorage.getItemFromStorage(item.getId()))
                .thenReturn(item);

        Mockito.when(mockItemStorage.save(Mockito.any(Item.class)))
                .thenReturn(mewItem);

        itemDto.setName(null);
        itemDto.setDescription(null);
        itemDto.setAvailable(null);

        ItemDto updatedItemDto = itemService.updateItem(itemDto, item.getId(), user.getId());

        assertThat(updatedItemDto.getName(), equalTo(mewItem.getName()));
        assertThat(updatedItemDto.getDescription(), equalTo(mewItem.getDescription()));
        assertThat(updatedItemDto.getAvailable(), equalTo(mewItem.getAvailable()));
    }

    @Test
    public void updateWithWrongOwner() {

        User foreignUser = new User();
        foreignUser.setId(2L);
        foreignUser.setName("Name");

        item.setOwner(foreignUser);

        Mockito.when(mockItemStorage.getItemFromStorage(item.getId()))
                .thenReturn(item);

        assertThrows(NotFoundException.class, () -> itemService.updateItem(itemDto, item.getId(), user.getId()));

    }

    @Test
    void getItemWithWrongUser() {
        Mockito.when(mockUserStorage.getUserFromStorage(Mockito.anyLong()))
                .thenThrow(new NotFoundException("user not found"));

        assertThrows(NotFoundException.class, () -> itemService.getItem(user.getId(), item.getId()));
    }

    @Test
    void getItemWithWrongItem() {

        Mockito.when(mockUserStorage.getUserFromStorage(Mockito.anyLong()))
                .thenReturn(user);

        Mockito.when(mockItemStorage.getItemWithCommentsFromStorage(item.getId()))
                .thenThrow(new NotFoundException("item not found"));

        assertThrows(NotFoundException.class, () -> itemService.getItem(user.getId(), item.getId()));
    }

    @Test
    void geItemForeignUser() {
        Mockito.when(mockUserStorage.getUserFromStorage(Mockito.anyLong()))
                .thenReturn(booker);

        Mockito.when(mockItemStorage.getItemWithCommentsFromStorage(item.getId()))
                .thenReturn(item);

        ItemDto itemDto1 = itemService.getItem(user.getId(), item.getId());

        assertThat(itemDto1.getName(), equalTo(item.getName()));
        assertThat(itemDto1.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDto1.getAvailable(), equalTo(item.getAvailable()));
    }


    @Test
    void getItem() {
        Mockito.when(mockUserStorage.getUserFromStorage(Mockito.anyLong()))
                .thenReturn(user);

        Mockito.when(mockItemStorage.getItemWithCommentsFromStorage(item.getId()))
                .thenReturn(item);

        ItemDto itemDto1 = itemService.getItem(user.getId(), item.getId());

        assertThat(itemDto1.getName(), equalTo(item.getName()));
        assertThat(itemDto1.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDto1.getAvailable(), equalTo(item.getAvailable()));

    }

    @Test
    void findItems() {

        Mockito.when(mockUserStorage.getUserFromStorage(Mockito.anyLong()))
                .thenReturn(user);

        Mockito.when(mockItemStorage.findItems(Mockito.anyString(), Mockito.any(PageRequest.class)))
                .thenReturn(List.of(item));

        List<ItemDto> items = itemService.findItems(user.getId(), "thing", 1, 1);

        assertThat(items.get(0).getName(), equalTo(item.getName()));
        assertThat(items.get(0).getDescription(), equalTo(item.getDescription()));
    }

    @Test
    void findItemsWithEmptyQuery() {

        Mockito.when(mockUserStorage.getUserFromStorage(Mockito.anyLong()))
                .thenReturn(user);

        List<ItemDto> items = itemService.findItems(user.getId(), "", 1, 1);

        assertThat(items.size(), equalTo(0));
    }

    @Test
    void getAllItems() {
        Mockito.when(mockItemStorage.findAllByOwner_IdOrderById(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(item));

        Mockito.when(mockBookingStorage.findByItem_IdInOrderByItem_IdAscStartAsc(Mockito.anyCollection()))
                .thenReturn(List.of(lastBooking, nextBooking));

        List<ItemDto> items = itemService.getAllItems(user.getId(), 1, 1);

        assertThat(items.get(0).getDescription(), equalTo(item.getDescription()));
        assertThat(items.get(0).getLastBooking().getId(), equalTo(lastBooking.getId()));
        assertThat(items.get(0).getNextBooking().getStart(), equalTo(nextBooking.getStart()));
    }

    @Test
    void getAllItemsWithSomeAndNextLastBooking() {

        Mockito.when(mockItemStorage.findAllByOwner_IdOrderById(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(item));

        Booking lastBooking2 = new Booking();
        lastBooking2.setId(1L);
        lastBooking2.setItem(item);
        lastBooking2.setStatus(Status.APPROVED);
        lastBooking2.setStart(LocalDateTime.of(2022, 1, 1, 0, 0));
        lastBooking2.setEnd(LocalDateTime.of(2022, 1, 1, 0, 0));
        lastBooking2.setBooker(booker);

        Booking nextBooking2 = new Booking();
        nextBooking2.setId(1L);
        nextBooking2.setItem(item);
        nextBooking2.setStatus(Status.APPROVED);
        nextBooking2.setStart(LocalDateTime.of(3022, 1, 1, 0, 0));
        nextBooking2.setEnd(LocalDateTime.of(3022, 1, 1, 0, 0));
        nextBooking2.setBooker(booker);

        Mockito.when(mockBookingStorage.findByItem_IdInOrderByItem_IdAscStartAsc(Mockito.anyCollection()))
                .thenReturn(List.of(lastBooking2, lastBooking, nextBooking, nextBooking2));

        List<ItemDto> items = itemService.getAllItems(user.getId(), 1, 1);

        assertThat(items.get(0).getDescription(), equalTo(item.getDescription()));
        assertThat(items.get(0).getLastBooking().getId(), equalTo(lastBooking.getId()));
        assertThat(items.get(0).getNextBooking().getStart(), equalTo(nextBooking.getStart()));
    }

    @Test
    void createCommentWithWrongUser() {
        Mockito.when(mockUserStorage.getUserFromStorage(Mockito.anyLong()))
                .thenThrow(new NotFoundException("user not found"));

        assertThrows(NotFoundException.class, () -> itemService.createComment(user.getId(), item.getId(), commentDto));
    }

    @Test
    void createCommentWithWrongItem() {

        Mockito.when(mockUserStorage.getUserFromStorage(Mockito.anyLong()))
                .thenReturn(user);

        Mockito.when(mockItemStorage.getItemFromStorage(Mockito.anyLong()))
                .thenThrow(new NotFoundException("user not found"));

        assertThrows(NotFoundException.class, () -> itemService.createComment(user.getId(), item.getId(), commentDto));
    }

    @Test
    void createCommentWithoutBooking() {
        Mockito.when(mockUserStorage.getUserFromStorage(Mockito.anyLong()))
                .thenReturn(user);

        Mockito.when(mockItemStorage.getItemFromStorage(Mockito.anyLong()))
                .thenReturn(item);


        Mockito.when(mockBookingStorage.findByBooker_IdAndItem_IdAndStartBefore(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(LocalDateTime.class)))
                .thenReturn(List.of());

        assertThrows(BadRequestException.class, () -> itemService.createComment(user.getId(), item.getId(), commentDto));
    }

    @Test
    void createComment() {
        Mockito.when(mockUserStorage.getUserFromStorage(Mockito.anyLong()))
                .thenReturn(user);

        Mockito.when(mockItemStorage.getItemFromStorage(Mockito.anyLong()))
                .thenReturn(item);


        Mockito.when(mockBookingStorage.findByBooker_IdAndItem_IdAndStartBefore(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(LocalDateTime.class)))
                .thenReturn(List.of(lastBooking));

        Mockito.when(mockCommentStorage.save(Mockito.any(Comment.class)))
                .thenReturn(comment);

        CommentDto savedComment = itemService.createComment(user.getId(), item.getId(), commentDto);

        assertThat(savedComment.getText(), equalTo(commentDto.getText()));
        assertThat(savedComment.getAuthorName(), equalTo(commentDto.getAuthorName()));
    }

    private ItemDto createItemDto(String name, String description, boolean available) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(available);

        return itemDto;
    }
}