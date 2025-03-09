package booking.dto;

import booking.model.BookingStatus;
import item.dto.ItemDto;
import lombok.*;
import lombok.experimental.FieldDefaults;
import user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OutputBookingDto {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    ItemDto item;
    UserDto booker;
    BookingStatus status;
}