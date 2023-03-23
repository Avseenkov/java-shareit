package ru.practicum.shareit.booking.service;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.QBooking;
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

@Service
@AllArgsConstructor
public class BookingServiceImp implements BookingService {

    private final BookingStorage bookingStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Override
    @Transactional
    public BookingDto create(BookingPlainDto bookingPlainDto, Long userId) {
        User user = userStorage.getUserFromStorage(userId);
        Item item = itemStorage.getItemFromStorage(bookingPlainDto.getItemId());

        if (user.getId().equals(item.getOwner().getId())) {
            throw new NotFoundException("It is prohibited to create booking for your item");
        }
        if (!item.getAvailable()) {
            throw new BadRequestException("Item is not available");
        }

        if (bookingPlainDto.getEnd().isBefore(bookingPlainDto.getStart())
                || bookingPlainDto.getEnd().equals(bookingPlainDto.getStart())) {
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
    @Transactional
    public BookingDto setApprove(Long bookingId, Long userId, boolean approve) {
        Status status = approve ? Status.APPROVED : Status.REJECTED;
        Booking booking = bookingStorage.getBookingFromStorage(bookingId);

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
    @Transactional(readOnly = true)
    public BookingDto getBooking(Long bookingId, Long userId) {
        User user = userStorage.getUserFromStorage(userId);
        Booking booking = bookingStorage.getBookingFromStorage(bookingId);
        if (!user.getId().equals(booking.getBooker().getId()) && !user.getId().equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException(String.format("User doesn't  have the access to booking with id %s", bookingId));
        }
        return BookingMapper.bookingDtoFromBooking(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookings(Long userId, String query, int from, int size) {

        User user = userStorage.getUserFromStorage(userId);

        BooleanExpression byBookerId = QBooking.booking.booker.id.eq(user.getId());
        OrderSpecifier<LocalDateTime> startDesc = QBooking.booking.start.desc();

        int page = from / size;

        Iterable<Booking> bookings = getBookings(query, byBookerId, startDesc, page, size);

        return BookingMapper.mapToBookingDto(bookings);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getOwnerBookings(Long userId, String query, int from, int size) {

        User user = userStorage.getUserFromStorage(userId);

        BooleanExpression byItemOwnerId = QBooking.booking.item.owner.id.eq(user.getId());
        OrderSpecifier<LocalDateTime> startDesc = QBooking.booking.start.desc();

        int page = from / size;

        Iterable<Booking> bookings = getBookings(query, byItemOwnerId, startDesc, page, size);

        return BookingMapper.mapToBookingDto(bookings);
    }

    private Iterable<Booking> getBookings(
            String query,
            BooleanExpression byUserId,
            OrderSpecifier<LocalDateTime> sort,
            int page,
            int size
    ) {
        Page<Booking> bookings;
        Sort sortBy = Sort.by(Sort.Direction.DESC, "start");
        PageRequest pageRequest = PageRequest.of(page, size, sortBy);
        switch (query) {
            case "CURRENT": {

                bookings = bookingStorage.findAll(
                        byUserId
                                .and(QBooking.booking.start.before(LocalDateTime.now()))
                                .and(QBooking.booking.end.after(LocalDateTime.now())),
                        pageRequest
                );

                break;
            }
            case "PAST": {

                bookings = bookingStorage.findAll(
                        byUserId
                                .and(QBooking.booking.end.before(LocalDateTime.now())),
                        pageRequest
                );
                break;
            }

            case "FUTURE": {
                bookings = bookingStorage.findAll(
                        byUserId
                                .and(QBooking.booking.start.after(LocalDateTime.now())),
                        pageRequest
                );
                break;
            }

            case "WAITING": {

                bookings = bookingStorage.findAll(
                        byUserId
                                .and(QBooking.booking.status.eq(Status.WAITING)),
                        pageRequest
                );
                break;
            }

            case "REJECTED": {
                bookings = bookingStorage.findAll(
                        byUserId
                                .and(QBooking.booking.status.eq(Status.REJECTED)),
                        pageRequest
                );
                break;
            }

            case "ALL": {
                bookings = bookingStorage.findAll(
                        byUserId,
                        pageRequest
                );
                break;
            }
            default:
                throw new BadRequestException("Unknown state: " + query);
        }
        return bookings.getContent();
    }

}
