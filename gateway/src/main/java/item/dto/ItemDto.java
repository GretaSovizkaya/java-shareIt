package item.dto;

import booking.dto.BookingDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jdk.jfr.BooleanFlag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import request.model.ItemRequest;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    Long id;
    @NotBlank(message = "Наименование должен быть указано")
    String name;
    @NotBlank(message = "Описание должно быть указано")
    String description;
    @BooleanFlag
    @NotNull(message = "Доступность не может быть null")
    Boolean available;
    Long requestId;
}