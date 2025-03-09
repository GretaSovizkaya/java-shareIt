package item.controller;


import item.ItemClient;
import item.dto.CommentRequestDto;
import item.dto.ItemDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private ItemClient client;
    static final String userParamHeader = "X-Sharer-User-Id";

    @GetMapping("/{id}")
    public ResponseEntity<Object> get(@RequestHeader(userParamHeader) Long userId, @PathVariable Long id) {
        log.info("Get item {}, userId={}", id, userId);
        return client.getItem(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getByOwnerId(@RequestHeader(userParamHeader) Long userId) {
        log.info("Get getByOwnerId ={}", userId);
        return client.getByOwnerId(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemBySearch(@RequestParam String text) {
        log.info("==>getItemBySearch : {}", text);

        return client.findBySearch(text);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(userParamHeader) Long userId,
                                         @RequestBody @Valid ItemDto itemDto) {
        log.info("==>create Item: {} с владельцем {}", itemDto, userId);
        return client.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(userParamHeader) Long userId,
                                         @PathVariable Long itemId,
                                         @RequestBody ItemDto itemDto) {
        log.info("==>update Item: {} владельца {}", itemDto, userId);
        itemDto.setId(itemId);
        return client.update(itemDto, userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("==>Delete Item по: {}", id);
        client.delete(id);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(userParamHeader) Long userId,
                                                @PathVariable Long itemId,
                                                @RequestBody CommentRequestDto commentRequestDto) {
        log.info("==>Create comment to Item id: {}", itemId);
        return client.addComment(userId, itemId, commentRequestDto);

    }
}