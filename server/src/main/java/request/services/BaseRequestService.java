package request.services;

import booking.mapper.BookingMapper;
import exceptions.NotFoundException;
import item.mapper.ItemMapper;
import item.model.Item;
import item.repository.ItemRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import request.dto.ItemRequestDto;
import request.dto.ItemRequestInfoDto;

import request.mapper.RequestMapper;
import request.model.ItemRequest;
import request.repository.ItemRequestRepository;
import user.model.User;
import user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class BaseRequestService implements ItemRequestService {
    ItemRequestRepository itemRequestRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;
    ItemMapper itemMapper;
    BookingMapper bookingMapper;
    RequestMapper requestMapper;

    @Override
    public List<ItemRequestInfoDto> findAllByUserId(Long userId) {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorId(userId);
        List<Long> itemRequestIds = itemRequests.stream().map(ItemRequest::getId).collect(Collectors.toList());
        List<Item> items = itemRepository.findAllByItemRequestIds(itemRequestIds);

        Map<Long, List<Item>> itemsByRequestId = items.stream()
                .collect(Collectors.groupingBy(item -> item.getItemRequest().getId()));

        return itemRequests.stream()
                .map(itemRequest -> {
                    ItemRequestInfoDto itemRequestInfoDto = requestMapper.toItemRequestDto(itemRequest);
                    itemRequestInfoDto.setItems(itemMapper.toItemsDtoForRequest(itemsByRequestId.getOrDefault(itemRequest.getId(), Collections.emptyList())));
                    return itemRequestInfoDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestInfoDto create(ItemRequestDto itemRequestRequestDto) {
        User requestor = userRepository.findById(itemRequestRequestDto.getRequestorId()).orElseThrow(() -> {
            throw new NotFoundException("User с id = " + itemRequestRequestDto.getRequestorId() + " не найден!");
        });


        ItemRequest itemRequest = itemRequestRepository.save(
                ItemRequest.builder()
                        .description(itemRequestRequestDto.getDescription())
                        .requestor(requestor)
                        .created(LocalDateTime.now())
                        .build()
        );
        log.info("Запрос создан");
        return requestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public ItemRequestInfoDto findItemRequestById(Long itemRequestId, Long userId) {
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException(String.format("Request с id = %d не найден.", itemRequestId)));

        List<Item> items = itemRepository.findAllByItemRequest(itemRequest);
        ItemRequestInfoDto itemRequestInfoDto = requestMapper.toItemRequestDto(itemRequest);
        itemRequestInfoDto.setItems(itemMapper.toItemsDtoForRequest(items));
        return itemRequestInfoDto;
    }

    @Override
    public List<ItemRequestInfoDto> findAllUsersItemRequest() {
        List<ItemRequest> itemRequests = itemRequestRepository.findAll();
        List<Long> itemRequestIds = itemRequests.stream().map(ItemRequest::getId).collect(Collectors.toList());
        List<Item> items = itemRepository.findAllByItemRequestIds(itemRequestIds);

        Map<Long, List<Item>> itemsByRequestId = items.stream()
                .collect(Collectors.groupingBy(item -> item.getItemRequest().getId()));

        return itemRequests.stream()
                .map(itemRequest -> {
                    ItemRequestInfoDto itemRequestInfoDto = requestMapper.toItemRequestDto(itemRequest);
                    itemRequestInfoDto.setItems(itemMapper.toItemsDtoForRequest(itemsByRequestId.getOrDefault(itemRequest.getId(), Collections.emptyList())));
                    return itemRequestInfoDto;
                })
                .collect(Collectors.toList());
    }
}