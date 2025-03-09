package item.controller;

import item.dto.ItemDto;
import item.services.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService service;
    private static final String userParamHeader = "X-Sharer-User-Id";

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> get(@PathVariable Long id) {
        log.info("Получаем предмет по id: {}", id);
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    public ResponseEntity<Collection<ItemDto>> getByOwnerId(@RequestHeader(userParamHeader) Long userId) {
        log.info("Получаем предметы по владельцу: {}", userId);
        return ResponseEntity.ok(service.findByOwner(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<ItemDto>> getItemBySearch(@RequestParam String text) {
        log.info("Ищем предметы по запросу: {}", text);
        return ResponseEntity.ok(service.findBySearch(text));
    }

    @PostMapping
    public ResponseEntity<ItemDto> create(@RequestHeader(userParamHeader) Long userId,
                                          @RequestBody @Valid ItemDto itemDto) {
        log.info("Добавляем предмет: {} с владельцем {}", itemDto, userId);
        return ResponseEntity.ok(service.addNewItem(itemDto, userId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(@RequestHeader(userParamHeader) Long userId,
                                          @PathVariable Long itemId,
                                          @RequestBody ItemDto itemDto) {
        log.info("Обновляем сведения по предмету: {} вместе с владельцем {}", itemDto, userId);
        return ResponseEntity.ok(service.updateItem(itemId, itemDto, userId));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Удаляем предмет по айди: {}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}