package request.services;

import request.dto.ItemRequestDto;
import request.dto.ItemRequestInfoDto;

import java.util.List;

public interface ItemRequestService {
    List<ItemRequestInfoDto> findAllByUserId(Long userId);

    ItemRequestInfoDto create(ItemRequestDto itemRequestRequestDto);

    ItemRequestInfoDto findItemRequestById(Long itemRequestId, Long userId);

    List<ItemRequestInfoDto> findAllUsersItemRequest();
}