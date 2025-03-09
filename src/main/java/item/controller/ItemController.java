package item.controller;

import item.dto.CommentDto;
import item.dto.CommentInfoDto;
import item.dto.ItemDto;
import item.services.ItemService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService service;
    private static final String USER_PARAM_HEADER = "X-Sharer-User-Id";

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> get(@RequestHeader(USER_PARAM_HEADER) Long userId,
                                       @PathVariable Long id) {
        log.info("Получение Item по id: {}", id);
        return ResponseEntity.ok(service.findById(id, userId));
    }

    @GetMapping
    public ResponseEntity<Collection<ItemDto>> getByOwnerId(@RequestHeader(USER_PARAM_HEADER) Long userId) {
        log.info("Получение Item по владельцу: {}", userId);
        return ResponseEntity.ok(service.findByOwner(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<ItemDto>> getItemBySearch(@RequestParam String text) {
        log.info("Получение Item по поиску: {}", text);
        return ResponseEntity.ok(service.findBySearch(text));
    }

    @PostMapping
    public ResponseEntity<ItemDto> create(@RequestHeader(USER_PARAM_HEADER) Long userId,
                                          @RequestBody @Valid ItemDto itemDto) {
        log.info("Создание Item: {} с владельцем {}", itemDto, userId);
        ItemDto newItemDto = service.create(itemDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newItemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(@RequestHeader(USER_PARAM_HEADER) Long userId,
                                          @PathVariable Long itemId,
                                          @RequestBody ItemDto itemDto) {
        log.info("Обновление Item: {} владельца {}", itemDto, userId);
        itemDto.setId(itemId);
        return ResponseEntity.ok(service.update(itemDto, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Удаление Item по id: {}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> createComment(@NotNull @RequestHeader(USER_PARAM_HEADER) Long userId,
                                                    @PathVariable Long itemId,
                                                    @RequestBody @Valid CommentInfoDto commentInfoDto) {
        log.info("Создание комментария к Item с id: {}", itemId);
        CommentDto commentDto = service.createComment(itemId, userId, commentInfoDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentDto);
    }
}
