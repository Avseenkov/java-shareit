package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    UserStorage userStorage;
    ItemStorage itemStorage;
    BookingStorage bookingStorage;
    CommentStorage commentStorage;

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        Item item = ItemMapper.itemFromItemDto(itemDto);
        User user = getUserFromStorage(userId);
        item.setOwner(user);
        return ItemMapper.itemDtoFromItem(itemStorage.save(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        Item item = getStorageItem(itemId);
        User owner = item.getOwner();

        if (owner.getId() != userId) {
            throw new NotFoundException("Item not found");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        return ItemMapper.itemDtoFromItem(itemStorage.save(item));
    }

    @Override
    public ItemDto getItem(long userId, long itemId) {

        User user = getUserFromStorage(userId);
        Item item = getStorageItem(itemId);

        ItemDto itemDto = ItemMapper.itemDtoFromItem(item);

        if (user.getId().equals(item.getOwner().getId())) {
            setBookingsToItem(itemDto);
        }

        List<Comment> comments = commentStorage.findByItem_Id(itemId);

        itemDto.setComments(comments.stream().map(CommentMapper::commentDtoFromComment).collect(Collectors.toList()));

        return itemDto;
    }

    @Override
    public List<ItemDto> findItems(long userId, String query) {

        User user = getUserFromStorage(userId);

        if (query == null || query.isBlank() || query.isEmpty()) {
            return List.of();
        }
        return itemStorage.findItems(query).stream().map(ItemMapper::itemDtoFromItem).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAllItems(long id) {
        return itemStorage.findAllByOwner_IdOrderById(id).stream().map(ItemMapper::itemDtoFromItem)
                .map(this::setBookingsToItem)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(long userId, long itemId, CommentDto commentDto) {
        User user = getUserFromStorage(userId);
        Item item = getStorageItem(itemId);
        Comment comment = CommentMapper.commentFromCommentDto(commentDto);
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(user);
        comment.setItem(item);

        List<Booking> bookings = bookingStorage.findByBooker_IdAndItem_IdAndStartBefore(userId, itemId, LocalDateTime.now());

        if (bookings.isEmpty()) {
            throw new BadRequestException("there are no bookings for item");
        }

        return CommentMapper.commentDtoFromComment(commentStorage.save(comment));
    }

    private Item getStorageItem(long itemId) {
        return itemStorage.findById(itemId).orElseThrow(() -> new NotFoundException(String.format("Item with id = %s not found", itemId)));
    }

    private User getUserFromStorage(long id) {
        return userStorage.findById(id).orElseThrow(() -> new NotFoundException(String.format("User with id = %s not found", id)));
    }

    private ItemDto setBookingsToItem(ItemDto itemDto) {

        Optional<Booking> lastBooking = bookingStorage.findFirstByItem_IdAndStartBeforeOrderByStartDesc(itemDto.getId(), LocalDateTime.now());
        Optional<Booking> nextBooking = bookingStorage.findFirstByItem_IdAndStartAfter(itemDto.getId(), LocalDateTime.now());

        lastBooking.ifPresent(booking -> itemDto.setLastBooking(BookingMapper.bookingPlainDtoFromBooking(lastBooking.get())));
        nextBooking.ifPresent(booking -> itemDto.setNextBooking(BookingMapper.bookingPlainDtoFromBooking(nextBooking.get())));

        return itemDto;
    }
}



