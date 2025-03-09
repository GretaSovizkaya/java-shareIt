package item.mapper;

import booking.mapper.BookingMapper;
import item.dto.ItemDto;
import item.model.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {BookingMapper.class})
public interface ItemMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    @Mapping(target = "request", source = "request")
    @Mapping(target = "lastBooking", ignore = true) // lastBooking и nextBooking добавляются в сервисе
    @Mapping(target = "nextBooking", ignore = true)
    @Mapping(target = "comments", ignore = true) // Comments добавляются в сервисе
    ItemDto toItemDto(Item item);

    @Mapping(target = "owner", ignore = true) // owner связывается в сервисе
    @Mapping(target = "request", ignore = true) // request связывается в сервисе
    Item toItem(ItemDto itemDto);
}
