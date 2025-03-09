package booking.services;

import booking.dto.BookingDto;
import booking.dto.OutputBookingDto;
import booking.mapper.BookingMapper;
import booking.model.Booking;
import booking.model.BookingStatus;
import booking.repository.BookingRepository;
import exceptions.NotFoundException;
import exceptions.ValidatetionConflict;
import exceptions.ValidationException;
import item.model.Item;
import item.repository.ItemRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import user.mapper.UserMapper;
import user.model.User;
import user.repository.UserRepository;
import user.services.UserService;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingServiceImpl implements BookingService {
    BookingRepository bookingRepository;
    ItemRepository itemRepository;
    UserRepository userRepository;
    UserService userService;
    UserMapper userMapper;
    BookingMapper bookingMapper;
    private static final Sort NEWEST_FIRST = Sort.by(Sort.Direction.DESC, "start");

    @Override
    @Transactional
    public void delete(long id) {
        Booking booking = getBooking(id);
        bookingRepository.delete(booking);
    }

    @Override
    @Transactional
    public OutputBookingDto create(BookingDto bookingDto, long bookerId) {
        User booker = userMapper.toUser(userService.findById(bookerId));
        bookingDto.setBooker(booker.getId());
        Item item = getItem(bookingDto.getItemId());

        if (!item.getAvailable()) {
            throw new ValidationException("Item недоступен");
        }
        if (bookingRepository.isAvailable(item.getId(), bookingDto.getStart(), bookingDto.getEnd())) {
            throw new ValidationException("Item на эти даты недоступен");
        }
        if (item.getOwner().getId().equals(bookerId)) {
            throw new ValidationException("Владелец не может создать booking");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidationException("Дата окончания бронирования не может быть раньше даты начала");
        }
        bookingDto.setBooker(booker.getId());

        Booking booking = Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        return bookingMapper.toOutputBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public OutputBookingDto approve(long bookingId, boolean approved, long ownerId) {
        Booking booking = getBooking(bookingId);
        Item item = booking.getItem();

        if (!item.getOwner().getId().equals(ownerId)) {
            throw new ValidationException("Подтвердить Booking может только владелец");
        }
        if (booking.getStatus().equals(BookingStatus.WAITING)) {
            booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        } else {
            throw new ValidationException("Booking в статусе " + booking.getStatus());
        }
        return bookingMapper.toOutputBookingDto(bookingRepository.save(booking));
    }


    @Override
    @Transactional(readOnly = true)
    public OutputBookingDto findById(long bookingId, long userId) {
        Booking booking = getBooking(bookingId);
        OutputBookingDto bookingDto;
        Item item = getItem(booking.getItem().getId());
        if (item.getOwner().getId().equals(userId) || booking.getBooker().getId().equals(userId)) {
            bookingDto = bookingMapper.toOutputBookingDto(booking);
        } else {
            throw new ValidationException("Booking  доступен только Owner и Booker");
        }
        return bookingDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OutputBookingDto> findByBookerId(long bookerId, String status) {
        User booker = getUser(bookerId);
        BookingStatus bookingStatus = BookingStatus.from(status);
        if (bookingStatus == null) {
            throw new ValidatetionConflict("Некорректный статус Booking: " + status);
        }

        List<Booking> listBooking;
        switch (bookingStatus) {
            case ALL:
                listBooking = bookingRepository.findByBooker(booker, NEWEST_FIRST);
                break;
            case CURRENT:
                listBooking = bookingRepository.findByBookerAndEndAfterAndStartBefore(booker, LocalDateTime.now(), LocalDateTime.now(), NEWEST_FIRST);
                break;
            case FUTURE:
                listBooking = bookingRepository.findByBookerAndStartAfter(booker, LocalDateTime.now(), NEWEST_FIRST);
                break;
            case PAST:
                listBooking = bookingRepository.findByBookerAndEndBefore(booker, LocalDateTime.now(), NEWEST_FIRST);
                break;
            case WAITING:
            case REJECTED:
            case APPROVED:
                listBooking = bookingRepository.findByBookerAndStatusEquals(booker, status, NEWEST_FIRST);
                break;
            default:
                throw new ValidatetionConflict("Некорректный статус Booking: " + status);
        }

        return listBooking.stream()
                .map(bookingMapper::toOutputBookingDto)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<OutputBookingDto> findByOwnerId(long ownerId, String status) {
        List<Booking> listBooking = new ArrayList<>();
        User owner = getUser(ownerId);
        if (BookingStatus.from(status) == null) {
            throw new ValidatetionConflict("Некорректный статус Booking" + status);
        }
        BookingStatus bookingStatus = BookingStatus.from(status);
        switch (bookingStatus) {
            case ALL:
                listBooking = bookingRepository.findByItemOwner(owner, NEWEST_FIRST);
                break;
            case CURRENT:
                listBooking = bookingRepository.findByItemOwnerAndEndAfterAndStartBefore(owner, LocalDateTime.now(), LocalDateTime.now(), NEWEST_FIRST);
                break;
            case FUTURE:
                listBooking = bookingRepository.findByItemOwnerAndStartAfter(owner, LocalDateTime.now(), NEWEST_FIRST);
                break;
            case PAST:
                listBooking = bookingRepository.findByItemOwnerAndEndBefore(owner, LocalDateTime.now(), NEWEST_FIRST);
                break;
            case WAITING:
            case REJECTED:
            case APPROVED:
                listBooking = bookingRepository.findByItemOwnerAndStatusEquals(owner, status, NEWEST_FIRST);
                break;
        }
        return listBooking.stream()
                .map(bookingMapper::toOutputBookingDto)
                .toList();
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: не найден:" + userId));

    }

    private Item getItem(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item  с id: не найден: " + itemId));
    }

    private Booking getBooking(long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking с id: не найден: " + bookingId));
    }
}