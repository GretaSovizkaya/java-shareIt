package booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import booking.dto.BookingDto;
import booking.dto.OutputBookingDto;
import booking.model.Booking;
import item.mapper.ItemMapper;
import user.mapper.UserMapper;

@Mapper
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    // Преобразуем Booking в BookingDto
    @Mapping(target = "itemId", source = "item.id")  // Маппим itemId из объекта Item
    @Mapping(target = "booker", source = "booker.id")  // Маппим booker из объекта User
    BookingDto toBookingDto(Booking booking);

    // Преобразуем Booking в OutputBookingDto
    @Mapping(target = "item", source = "item")  // Маппим объект Item в ItemDto
    @Mapping(target = "booker", source = "booker")  // Маппим объект User в UserDto
    OutputBookingDto toOutputBookingDto(Booking booking);
}
