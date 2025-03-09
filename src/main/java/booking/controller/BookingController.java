package booking.controller;

import booking.dto.BookingDto;
import booking.dto.OutputBookingDto;
import booking.services.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private static final String USER_PARAM_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<OutputBookingDto> create(@RequestHeader(USER_PARAM_HEADER) Long userId,
                                                   @RequestBody @Valid BookingDto bookingDto) {
        log.info("Создание Booking: {}", bookingDto);
        OutputBookingDto bookingDtoNew = bookingService.create(bookingDto, userId);
        return ResponseEntity.status(201).body(bookingDtoNew);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<OutputBookingDto> approve(@PathVariable Long bookingId,
                                                    @RequestParam boolean approved,
                                                    @RequestHeader(USER_PARAM_HEADER) Long ownerId) {
        log.info("Подтверждение Booking владельцем: {}", ownerId);
        OutputBookingDto bookingDto = bookingService.approve(bookingId, approved, ownerId);
        return ResponseEntity.ok(bookingDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<OutputBookingDto> getBooking(@PathVariable Long bookingId,
                                                       @RequestHeader(USER_PARAM_HEADER) Long userId) {
        log.info("Получение данных о бронировании: {}", bookingId);
        OutputBookingDto bookingDto = bookingService.findById(bookingId, userId);
        return ResponseEntity.ok(bookingDto);
    }

    @GetMapping
    public ResponseEntity<List<OutputBookingDto>> getBookings(@RequestParam(defaultValue = "ALL") String state,
                                                              @RequestHeader(USER_PARAM_HEADER) Long bookerId) {
        log.info("Получение бронирований пользователя: {}", bookerId);
        List<OutputBookingDto> listBookingDto = bookingService.findByBookerId(bookerId, state);
        return ResponseEntity.ok(listBookingDto);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<OutputBookingDto>> getOwnerBookings(@RequestParam(defaultValue = "ALL") String state,
                                                                   @RequestHeader(USER_PARAM_HEADER) Long ownerId) {
        log.info("Получение бронирований по владельцу: {}", ownerId);
        List<OutputBookingDto> listBookingDto = bookingService.findByOwnerId(ownerId, state);
        return ResponseEntity.ok(listBookingDto);
    }
}
