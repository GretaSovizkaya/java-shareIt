package booking.controller;

import booking.dto.BookingApproveDto;
import booking.dto.BookingDto;
import booking.dto.OutputBookingDto;
import booking.services.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingService bookingService;
    static final String userParmHeader = "X-Sharer-User-Id";

    @PostMapping
    public OutputBookingDto create(@RequestHeader(userParmHeader) Long userId,
                                   @RequestBody BookingDto bookingDto) {
        log.info("==>Создание Booking: ", bookingDto);
        OutputBookingDto bookingDtoNew = bookingService.create(bookingDto, userId);
        return bookingDtoNew;
    }

    @PatchMapping("/{bookingId}")
    public OutputBookingDto approve(@RequestHeader(userParmHeader) Long ownerId,
                                    @PathVariable(name = "bookingId") Long bookingId,
                                    @RequestParam(value = "approved") Boolean approved) {
        log.info("==> Подтверждение  Booking: владельцем :userId", bookingId, ownerId);
        BookingApproveDto bookingApproveDto = BookingApproveDto.builder()
                .id(bookingId)
                .approved(approved)
                .build();
        OutputBookingDto bookingDto = bookingService.approve(bookingApproveDto, ownerId);
        return bookingDto;
    }

    @GetMapping("/{bookingId}")
    public OutputBookingDto getBooking(@PathVariable(name = "bookingId") Long bookingId,
                                       @RequestHeader(userParmHeader) Long userId) {
        log.info("==> Получение данных о бронировании :bookingId", bookingId);
        OutputBookingDto bookingDto = bookingService.findById(bookingId, userId);
        return bookingDto;
    }

    @GetMapping
    public List<OutputBookingDto> getBookings(@RequestParam(value = "state", defaultValue = "ALL") String status,
                                              @RequestHeader(userParmHeader) Long bookerId) {
        log.info("==> Получение бронирований пользователя :bookingId", bookerId);
        List<OutputBookingDto> listBookingDto = bookingService.findByBookerId(bookerId, status);
        return listBookingDto;
    }

    @GetMapping("/owner")
    public List<OutputBookingDto> getOwnerBookings(@RequestParam(value = "state", defaultValue = "ALL") String status,
                                                   @RequestHeader(userParmHeader) Long ownerId) {
        log.info("==>  Получение бронирований по владельцу :ownerid", ownerId);

        List<OutputBookingDto> listBookingDto = bookingService.findByOwnerId(ownerId, status);
        return listBookingDto;
    }
}