package item.mapper;
import item.dto.ItemDto;
import item.dto.ItemForRequestDto;
import item.model.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getItemRequest() != null ? item.getItemRequest().getId() : null)
                .build();

    }

    public static Item toItem(ItemDto item) {

        return Item.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static List<ItemDto> toItemsDtoCollection(Collection<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    public static ItemForRequestDto toItemForRequestDto(Item item) {

        return ItemForRequestDto.builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner().getId())
                .build();

    }

    public static List<ItemForRequestDto> toItemsDtoForRequest(Collection<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemForRequestDto)
                .toList();
    }
}