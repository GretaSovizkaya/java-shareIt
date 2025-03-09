package user.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import user.dto.UserDto;
import user.services.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService service;

    @GetMapping("/{id}")
    public UserDto get(@PathVariable Long id) {
        log.info("Запрос User по id: {}", id);
        UserDto userDto = service.findById(id);
        return userDto;
    }

    @PostMapping()
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("==>Создание User: {}", userDto);
        UserDto newUserDto = service.create(userDto);
        return newUserDto;
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable Long id, @RequestBody UserDto userDto) {
        log.info("==>Обновление User: {}", userDto);
        userDto.setId(id);
        UserDto userUpdDto = service.update(userDto);
        return userUpdDto;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("==>Удалениее User по: {}", id);
        service.delete(id);
    }
}