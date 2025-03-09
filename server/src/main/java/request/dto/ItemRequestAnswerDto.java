package request.dto;

import item.dto.ItemDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestAnswerDto {
    String description;
    LocalDateTime created;
    List<ItemDto> items;
}