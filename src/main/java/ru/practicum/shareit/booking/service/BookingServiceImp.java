package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingPlainDto;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingServiceImp implements BookingService {

    BookingStorage bookingStorage;
    UserStorage userStorage;
    ItemStorage itemStorage;

    @Override
    public BookingDto create(BookingPlainDto bookingPlainDto, Long userId) {
        User user = getUserFromStorage(userId);
        Item item = getStorageAvailableItem(bookingPlainDto.getItemId());

        if (user.getId().equals(item.getOwner().getId())) {
            throw new NotFoundException("It is prohibited to create booking for your item");
        }
        if (!item.getAvailable()) {
            throw new BadRequestException("Item is not available");
        }

        if (bookingPlainDto.getEnd().isBefore(bookingPlainDto.getStart())) {
            throw new BadRequestException("End date has to be later than start data");
        }
        Booking booking = BookingMapper.bookingFromBookingPlainDto(bookingPlainDto);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        Booking savedBooking = bookingStorage.save(booking);
        return BookingMapper.bookingDtoFromBooking(savedBooking);

    }

    @Override
    public BookingDto setApprove(Long bookingId, Long userId, boolean approve) {
        Status status = approve ? Status.APPROVED : Status.REJECTED;
        Booking booking = getBookingFromStorage(bookingId);

        if (booking.getStatus().equals(status)) {
            throw new BadRequestException(String.format("There is not booking for set %s", status));
        }

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Wrong owner of the item");
        }

        booking.setStatus(status);
        return BookingMapper.bookingDtoFromBooking(bookingStorage.save(booking));

    }

    @Override
    public BookingDto getBooking(Long bookingId, Long userId) {
        User user = getUserFromStorage(userId);
        Booking booking = getBookingFromStorage(bookingId);
        if (!user.getId().equals(booking.getBooker().getId()) && !user.getId().equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException(String.format("User doesn't  have the access to booking with id %s", bookingId));
        }
        return BookingMapper.bookingDtoFromBooking(booking);
    }

    @Override
    public List<BookingDto> getBookings(Long userId, String query) {

        User user = getUserFromStorage(userId);

        switch (query) {
            case "CURRENT":
                return bookingStorage.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(user.getId(), LocalDateTime.now(), LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::bookingDtoFromBooking)
                        .collect(Collectors.toList());

            case "PAST":
                return bookingStorage.findALLByBookerIdAndEndBeforeOrderByStartDesc(user.getId(), LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::bookingDtoFromBooking)
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookingStorage.findAllByBookerIdAndStartAfterOrderByStartDesc(user.getId(), LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::bookingDtoFromBooking)
                        .collect(Collectors.toList());
            case "WAITING":
                return bookingStorage.findAllByBookerIdAndStatusOrderByStartDesc(user.getId(), Status.WAITING)
                        .stream()
                        .map(BookingMapper::bookingDtoFromBooking)
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookingStorage.findAllByBookerIdAndStatusOrderByStartDesc(user.getId(), Status.REJECTED)
                        .stream()
                        .map(BookingMapper::bookingDtoFromBooking)
                        .collect(Collectors.toList());
            case "ALL":
                return bookingStorage.findAllByBookerIdOrderByStartDesc(user.getId())
                        .stream()
                        .map(BookingMapper::bookingDtoFromBooking)
                        .collect(Collectors.toList());
            default:
                throw new BadRequestException("Unknown state: " + query);
        }

    }

    @Override
    public List<BookingDto> getOwnerBookings(Long userId, String query) {

        User user = getUserFromStorage(userId);

        switch (query) {
            case "CURRENT":
                return bookingStorage.findByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(user.getId(), LocalDateTime.now(), LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::bookingDtoFromBooking)
                        .collect(Collectors.toList());
            case "PAST":
                return bookingStorage.findByItem_Owner_IdAndEndBeforeOrderByStartDesc(user.getId(), LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::bookingDtoFromBooking)
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookingStorage.findByItem_Owner_IdAndStartAfterOrderByStartDesc(user.getId(), LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::bookingDtoFromBooking)
                        .collect(Collectors.toList());
            case "WAITING":
                return bookingStorage.findByItem_Owner_IdAndStatusOrderByStartDesc(user.getId(), Status.WAITING)
                        .stream()
                        .map(BookingMapper::bookingDtoFromBooking)
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookingStorage.findByItem_Owner_IdAndStatusOrderByStartDesc(user.getId(), Status.REJECTED)
                        .stream()
                        .map(BookingMapper::bookingDtoFromBooking)
                        .collect(Collectors.toList());
            case "ALL":
                return bookingStorage.findByItem_Owner_IdOrderByStartDesc(user.getId())
                        .stream()
                        .map(BookingMapper::bookingDtoFromBooking)
                        .collect(Collectors.toList());
            default:
                throw new BadRequestException("Unknown state: " + query);
        }
    }

    private User getUserFromStorage(long id) {
        return userStorage.findById(id).orElseThrow(() -> new NotFoundException(String.format("User with id = %s not found", id)));
    }

    private Item getStorageAvailableItem(long itemId) {
        return itemStorage.findById(itemId).orElseThrow(() -> new NotFoundException(String.format("Item with id = %s not found", itemId)));
    }

    private Booking getBookingFromStorage(long bookingId) {
        return bookingStorage.findById(bookingId).orElseThrow(() -> new NotFoundException(String.format("Booking with id = %s not found", bookingId)));
    }

}
