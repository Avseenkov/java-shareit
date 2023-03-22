package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class BookingStorageCustomImplTest {

    @SpyBean
    private BookingStorage bookingStorage;

    @Test
    void getBookingFromStorage() {

        Mockito.when(bookingStorage.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingStorage.getBookingFromStorage(1L));

    }
}