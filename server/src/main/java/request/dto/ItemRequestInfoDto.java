package request.dto;

import item.dto.ItemForRequestDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestInfoDto {
    Long id;
    String description;
    UserDto requestor;
    LocalDateTime created;
    List<ItemForRequestDto> items;
}