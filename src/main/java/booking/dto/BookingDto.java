package booking.dto;

import booking.model.BookingStatus;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDto {
     LocalDate start;
     LocalDate end;
     Long item;
     BookingStatus status;
}