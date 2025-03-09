package booking.dto;

import booking.model.BookingStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDto {
     Long id;
     @FutureOrPresent
     LocalDateTime start;
     @FutureOrPresent
     LocalDateTime end;
     @NotNull
     Long itemId;
     Long booker;
     BookingStatus status;
}