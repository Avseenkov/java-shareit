package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    UserStorage userStorage;
    ItemStorage itemStorage;
    BookingStorage bookingStorage;
    CommentStorage commentStorage;
    ItemRequestStorage itemRequestStorage;

    @Override
    @Transactional
    public ItemDto createItem(ItemDto itemDto, long userId) {
        Item item = ItemMapper.itemFromItemDto(itemDto);
        User user = userStorage.getUserFromStorage(userId);
        item.setOwner(user);
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = getItemRequestFromStorage(itemDto.getRequestId());
            item.setRequest(itemRequest);
        }
        return ItemMapper.itemDtoFromItem(itemStorage.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        Item item = itemStorage.getItemFromStorage(itemId);
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
    @Transactional(readOnly = true)
    public ItemDto getItem(long userId, long itemId) {

        User user = userStorage.getUserFromStorage(userId);
        Item item = itemStorage.getItemWithCommentsFromStorage(itemId);

        ItemDto itemDto = ItemMapper.itemDtoFromItem(item);

        if (user.getId().equals(item.getOwner().getId())) {
            setBookingsToItem(itemDto);
        }

        List<Comment> comments = (List<Comment>) item.getComments();

        itemDto.setComments(comments.stream().map(CommentMapper::commentDtoFromComment).collect(Collectors.toList()));

        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> findItems(long userId, String query, int from, int size) {

        User user = userStorage.getUserFromStorage(userId);

        if (query == null || query.isBlank() || query.isEmpty()) {
            return List.of();
        }
        return itemStorage.findItems(query, PageRequest.of(from, size)).stream().map(ItemMapper::itemDtoFromItem).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAllItems(long id, int from, int size) {
        Map<Long, ItemDto> itemsDto = itemStorage.findAllByOwner_IdOrderById(id, PageRequest.of(from, size)).stream().map(ItemMapper::itemDtoFromItem)
                .collect(Collectors.toMap(ItemDto::getId, itemDto -> itemDto));

        List<Booking> bookings = bookingStorage.findByItem_IdInOrderByItem_IdAscStartAsc(
                itemsDto.values().stream().map(ItemDto::getId).collect(Collectors.toList())
        );

        LocalDateTime now = LocalDateTime.now();

        bookings.forEach(booking -> {
            ItemDto itemDto = itemsDto.get(booking.getItem().getId());
            if (itemDto.getNextBooking() != null) {
                return;
            }
            if (booking.getStart().isBefore(now)) {
                if (itemDto.getLastBooking() == null) {
                    itemDto.setLastBooking(BookingMapper.bookingPlainDtoFromBooking(booking));
                    return;
                }

                if (itemDto.getLastBooking().getStart().isBefore(booking.getStart())) {
                    itemDto.setLastBooking(BookingMapper.bookingPlainDtoFromBooking(booking));
                    return;
                }
            }

            itemDto.setNextBooking(BookingMapper.bookingPlainDtoFromBooking(booking));

        });

        return new ArrayList<>(itemsDto.values());
    }

    @Override
    @Transactional
    public CommentDto createComment(long userId, long itemId, CommentDto commentDto) {
        User user = userStorage.getUserFromStorage(userId);
        Item item = itemStorage.getItemFromStorage(itemId);
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

    private ItemRequest getItemRequestFromStorage(long id) {
        return itemRequestStorage.findById(id).orElseThrow(() -> new NotFoundException(String.format("Item request with id = %s not found", id)));
    }

    private void setBookingsToItem(ItemDto itemDto) {

        Optional<Booking> lastBooking = bookingStorage.findFirstByItem_IdAndStartBeforeOrderByStartDesc(itemDto.getId(), LocalDateTime.now());
        Optional<Booking> nextBooking = bookingStorage.findFirstByItem_IdAndStartAfter(itemDto.getId(), LocalDateTime.now());

        lastBooking.ifPresent(booking -> itemDto.setLastBooking(BookingMapper.bookingPlainDtoFromBooking(lastBooking.get())));
        nextBooking.ifPresent(booking -> itemDto.setNextBooking(BookingMapper.bookingPlainDtoFromBooking(nextBooking.get())));

    }
}


