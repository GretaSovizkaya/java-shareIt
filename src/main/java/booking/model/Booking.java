package booking.model;

import jakarta.validation.constraints.FutureOrPresent;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class Booking {
    @FutureOrPresent
    LocalDate start;
    @FutureOrPresent
    LocalDate end;
    Long booker;
    Long item;
    BookingStatus status;
}