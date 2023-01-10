package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
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

    @PatchMapping("{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long id, @PathVariable long itemId, @RequestBody ItemDto itemDto) {
        return itemService.updateItem(itemDto, itemId, id);
    }

    @GetMapping("{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getItem(@PathVariable long itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping("search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> findItems(@RequestParam String text) {
        return itemService.findItems(text);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> findAll(@RequestHeader("X-Sharer-User-Id") Long id) {
        return itemService.getAllItems(id);
    }

}
