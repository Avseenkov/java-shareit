package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.booking.Booking;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingStorage extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking>, BookingStorageCustom {

    Optional<Booking> findFirstByItem_IdAndStartBeforeOrderByStartDesc(Long id, LocalDateTime start);

    Optional<Booking> findFirstByItem_IdAndStartAfter(Long id, LocalDateTime start);

    List<Booking> findByBooker_IdAndItem_IdAndStartBefore(Long id, Long otherId, LocalDateTime start);

    List<Booking> findByItem_IdInOrderByItem_IdAscStartAsc(Collection<Long> ids);


}
