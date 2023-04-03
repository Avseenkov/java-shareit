package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {

    ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long id, @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.createItemRequest(itemRequestDto, id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> getUserAllRequests(@RequestHeader("X-Sharer-User-Id") Long id) {
        return itemRequestService.getUserAllRequests(id);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> getAllForeignRequests(
            @RequestHeader("X-Sharer-User-Id") Long id,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "100") int size
    ) {
        return itemRequestService.getAllForeignRequests(id, from, size);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto getRequest(@RequestHeader("X-Sharer-User-Id") Long id, @PathVariable long requestId) {
        return itemRequestService.getRequest(id, requestId);
    }
}
