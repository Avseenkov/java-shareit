package ru.practicum.shareit.request;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("requests")
@AllArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long id, @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.createItemRequest(id, itemRequestDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getUserAllRequests(@RequestHeader("X-Sharer-User-Id") long id) {
        return itemRequestClient.getUserAllRequests(id);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllForeignRequests(
            @RequestHeader("X-Sharer-User-Id") long id,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "100") @Min(1) int size
    ) {
        return itemRequestClient.getAllForeignRequests(id, from, size);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getRequest(@RequestHeader("X-Sharer-User-Id") long id, @PathVariable @Positive long requestId) {
        return itemRequestClient.getRequest(id, requestId);
    }
}
