package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long id, @RequestBody @Valid ItemDto itemDto) {
        return itemService.createItem(itemDto, id);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long id, @PathVariable long itemId, @RequestBody ItemDto itemDto) {
        return itemService.updateItem(itemDto, itemId, id);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") Long id, @PathVariable long itemId) {
        return itemService.getItem(id, itemId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> findItems(
            @RequestHeader("X-Sharer-User-Id") Long id,
            @RequestParam String text,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "100") @Min(0) int size
    ) {
        return itemService.findItems(id, text, from, size);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> findAll(
            @RequestHeader("X-Sharer-User-Id") Long id,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "100") @Min(0) int size
    ) {
        return itemService.getAllItems(id, from, size);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long id, @PathVariable long itemId, @Valid @RequestBody CommentDto commentDto) {
        return itemService.createComment(id, itemId, commentDto);
    }

}
