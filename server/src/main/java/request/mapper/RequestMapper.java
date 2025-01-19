package request.mapper;

import item.mapper.ItemMapper;
import request.dto.ItemRequestInfoDto;
import request.model.ItemRequest;
import user.mapper.UserMapper;

import java.util.List;

public class RequestMapper {
    public static List<ItemRequestInfoDto> toItemRequestDtoList(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(RequestMapper::toItemRequestDto)
                .toList();
    }

    public static ItemRequestInfoDto toItemRequestDto(ItemRequest itemRequest) {
        if (itemRequest == null) return null;
        return ItemRequestInfoDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(UserMapper.toUserDto(itemRequest.getRequestor()))
                .created(itemRequest.getCreated())
                .items(ItemMapper.toItemsDtoForRequest(itemRequest.getItems() != null ? itemRequest.getItems() : List.of()))
                .build();
    }
}