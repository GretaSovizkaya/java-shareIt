package item.mapper;

import item.dto.ItemDto;
import item.model.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ItemMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    @Mapping(target = "request", source = "request.id")
    ItemDto toItemDto(Item item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", source = "ownerId")
    @Mapping(target = "request", ignore = true)
    Item toItem(ItemDto itemDto, long ownerId);
}
