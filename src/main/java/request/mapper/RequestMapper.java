package request.mapper;

import org.mapstruct.Mapper;
import request.dto.RequestDto;
import request.model.ItemRequest;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    RequestDto toItemRequestDto(ItemRequest itemRequest);
}
