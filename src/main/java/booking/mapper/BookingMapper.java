package booking.mapper;

import booking.dto.BookingDto;
import booking.dto.OutputBookingDto;
import booking.model.Booking;
import item.mapper.ItemMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import user.mapper.UserMapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class})
public interface BookingMapper {

    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    @Mapping(target = "itemId", source = "item.id")
    @Mapping(target = "booker", source = "booker.id")
    BookingDto toBookingDto(Booking booking);

    @Mapping(target = "item.id", source = "itemId")
    @Mapping(target = "booker.id", source = "booker")
    Booking toBooking(BookingDto bookingDto);

    @Mapping(target = "item", source = "item")
    @Mapping(target = "booker", source = "booker")
    OutputBookingDto toOutputBookingDto(Booking booking);
}
