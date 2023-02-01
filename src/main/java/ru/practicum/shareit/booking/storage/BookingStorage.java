package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingStorage extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long id);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long id, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long id, LocalDateTime start);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long id, Status status);

    List<Booking> findALLByBookerIdAndEndBeforeOrderByStartDesc(Long id, LocalDateTime start);

    List<Booking> findByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long id, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItem_Owner_IdOrderByStartDesc(Long id);

    List<Booking> findByItem_Owner_IdAndStartAfterOrderByStartDesc(Long id, LocalDateTime start);

    List<Booking> findByItem_Owner_IdAndStatusOrderByStartDesc(Long id, Status status);

    List<Booking> findByItem_Owner_IdAndEndBeforeOrderByStartDesc(Long id, LocalDateTime end);

    Optional<Booking> findFirstByItem_IdAndStartBeforeOrderByStartDesc(Long id, LocalDateTime start);

    Optional<Booking> findFirstByItem_IdAndStartAfter(Long id, LocalDateTime start);

    List<Booking> findByBooker_IdAndItem_IdAndStartBefore(Long id, Long id1, LocalDateTime start);

    List<Booking> findByItem_IdInOrderByItem_IdAscStartAsc(Collection<Long> ids);


}
