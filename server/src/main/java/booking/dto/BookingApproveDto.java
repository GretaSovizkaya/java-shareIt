package booking.dto;

import item.model.Item;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;


@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingApproveDto {
    @NotNull
    Long id;
    Item item;
    Boolean approved;
}