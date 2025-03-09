package booking.services;

import booking.dto.BookingApproveDto;
import booking.dto.BookingDto;
import booking.dto.OutputBookingDto;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public interface BookingService {
    OutputBookingDto create(BookingDto bookingDto, long id);

    OutputBookingDto approve(BookingApproveDto bookingApproveDto, long id);

    void delete(long id);

    OutputBookingDto findById(long bookingId, long userId);

    List<OutputBookingDto> findByBookerId(long bookerId, String status);

    List<OutputBookingDto> findByOwnerId(long ownerId, String status);
}