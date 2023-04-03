package ru.practicum.shareit.item;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@RestController()
@RequestMapping("items")
@AllArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") long id, @RequestBody @Valid ItemDto itemDto) {
        return itemClient.createItem(id, itemDto);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id")
                                             @Min(1) long id,
                                             @PathVariable long itemId,
                                             @RequestBody ItemDto itemDto) {
        return itemClient.updateItem(id, itemId, itemDto);
    }

    @GetMapping("{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id")
                                          @Min(1) long id,
                                          @PathVariable long itemId) {
        return itemClient.getItem(id, itemId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> findItems(
            @RequestHeader("X-Sharer-User-Id")
            @Min(1) long id,
            @RequestParam String text,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "100") @Min(0) int size
    ) {
        return itemClient.findItems(id, text, from, size);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> findAll(@RequestHeader("X-Sharer-User-Id")
                                          @Min(1) long id,
                                          @RequestParam(defaultValue = "0") @Min(0) int from,
                                          @RequestParam(defaultValue = "100") @Min(0) int size
    ) {
        return itemClient.findAll(id, from, size);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id")
                                                @Min(1) long id,
                                                @PathVariable long itemId, @Valid @RequestBody CommentDto commentDto
    ) {
        return itemClient.createComment(id, itemId, commentDto);
    }
}
