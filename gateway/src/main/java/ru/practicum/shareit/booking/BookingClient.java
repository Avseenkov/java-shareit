package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingPlainDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {

    public static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> createBooking(Long userId, BookingPlainDto bookingPlainDto) {
        return post("", userId, bookingPlainDto);
    }

    public ResponseEntity<Object> approveBooking(Long userId, Long bookingId, boolean approved) {
        Map<String, Object> params = Map.of(
                "approved", approved
        );
        return patch("/" + bookingId + "?approved={approved}", userId, params, null);
    }

    public ResponseEntity<Object> getBooking(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllBookings(Long userId, int from, int size, String state) {
        Map<String, Object> params = Map.of(
                "from", from,
                "size", size,
                "state", state
        );
        return get("/?from={from}&size={size}&state={state}", userId, params);
    }

    public ResponseEntity<Object> getAllOwnerBookings(Long userId, int from, int size, String state) {
        Map<String, Object> params = Map.of(
                "from", from,
                "size", size,
                "state", state
        );
        return get("/owner?from={from}&size={size}&state={state}", userId, params);
    }
}
