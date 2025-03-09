package request.mapper;

import item.mapper.ItemMapper;
import request.dto.ItemRequestInfoDto;
import request.model.ItemRequest;
import user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    List<ItemRequestInfoDto> toItemRequestDtoList(List<ItemRequest> itemRequests);
    ItemRequestInfoDto toItemRequestDto(ItemRequest itemRequest);
}
