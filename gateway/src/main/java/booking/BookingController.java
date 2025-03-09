package booking;

import booking.dto.BookingDto;
import booking.dto.BookingState;
import booking.dto.OutputBookingDto;
import booking.services.BookingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingController {
    BookingClient bookingClient;
    static final String userParamHeader = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(userParamHeader) Long userId,
                                         @RequestBody BookingDto bookingDto) {
        log.info("==>Creating Booking: ", bookingDto);
        return bookingClient.create(userId, bookingDto);
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(userParamHeader) Long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        try {
            BookingState state = BookingState.from(stateParam);
            log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
            return bookingClient.getBookings(userId, state, from, size);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(userParamHeader) Long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader(userParamHeader) Long ownerId,
                                          @PathVariable(name = "bookingId") Long bookingId,
                                          @RequestParam(value = "approved") Boolean approved) {
        log.info("==> Approve  Booking: by owner :userId", bookingId, ownerId);
        return bookingClient.approve(bookingId, approved, ownerId);
    }
}