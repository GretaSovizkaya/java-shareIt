package item.services;

import booking.mapper.BookingMapper;
import booking.model.Booking;
import booking.model.BookingStatus;
import booking.repository.BookingRepository;
import exceptions.NotFoundException;
import exceptions.ValidatetionConflict;
import exceptions.ValidationException;
import item.dto.CommentDto;
import item.dto.CommentInfoDto;
import item.dto.ItemDto;
import item.mapper.CommentMapper;
import item.mapper.ItemMapper;
import item.model.Comment;
import item.model.Item;
import item.repository.CommentRepository;
import item.repository.ItemRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import user.model.User;
import user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemServiceImpl implements ItemService {
    ItemRepository itemRepository;
    UserRepository userRepository;
    BookingRepository bookingRepository;
    CommentRepository commentRepository;
    ItemMapper itemMapper;
    CommentMapper commentMapper;

    @Override
    @Transactional
    public ItemDto create(ItemDto newItemDto, long ownerId) {
        User owner = getUser(ownerId);
        Item item = itemMapper.toItem(newItemDto);
        item.setOwner(owner);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto itemUpd, long ownerId) {
        User owner = getUser(ownerId);
        Item oldItem = getItem(itemUpd.getId());

        if (!oldItem.getOwner().equals(owner)) {
            throw new ValidatetionConflict("Некорректный владелец");
        }

        if (itemUpd.getName() != null) {
            oldItem.setName(itemUpd.getName());
        }
        if (itemUpd.getDescription() != null) {
            oldItem.setDescription(itemUpd.getDescription());
        }
        if (itemUpd.getAvailable() != null) {
            oldItem.setAvailable(itemUpd.getAvailable());
        }

        return itemMapper.toItemDto(itemRepository.save(oldItem));
    }

    @Override
    public void delete(long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto findById(long itemId, long userId) {
        Item item = getItem(itemId);
        ItemDto itemDto = itemMapper.toItemDto(item);

        if (item.getOwner().getId().equals(userId)) {
            setLastNextBooking(itemDto);
        }

        List<CommentDto> comments = commentRepository.findByItem(item)
                .stream()
                .map(commentMapper::toCommentDto)
                .toList();
        itemDto.setComments(comments);

        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> findByOwner(long ownerId) {
        User owner = getUser(ownerId);

        List<ItemDto> itemsDto = itemRepository.findByOwner(owner)
                .stream()
                .map(itemMapper::toItemDto)
                .toList();

        for (ItemDto itemDto : itemsDto) {
            setLastNextBooking(itemDto);

            List<CommentDto> comments = commentRepository.findByItem(getItem(itemDto.getId()))
                    .stream()
                    .map(commentMapper::toCommentDto)
                    .toList();
            itemDto.setComments(comments);
        }

        return itemsDto;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemDto> findBySearch(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.searchByNameOrDescription(text)
                .stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Transactional
    @Override
    public CommentDto createComment(long itemId, long userId, CommentInfoDto commentDto) {
        Item item = getItem(itemId);
        User user = getUser(userId);
        Booking booking = getBooking(user, item);

        if (!booking.getStatus().equals(BookingStatus.APPROVED) || booking.getEnd().isAfter(LocalDateTime.now())) {
            throw new ValidationException("Бронирование не завершено");
        }

        Comment comment = Comment.builder()
                .text(commentDto.getText())
                .item(item)
                .author(user)
                .created(commentDto.getCreated())
                .build();

        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    private void setLastNextBooking(ItemDto itemDto) {
        LocalDateTime now = LocalDateTime.now();

        List<Booking> approvedBookings = bookingRepository.findApprovedForItem(getItem(itemDto.getId()), Sort.by(Sort.Direction.DESC, "start"));

        Booking lastBooking = approvedBookings.stream()
                .filter(b -> b.getStart().isBefore(now))
                .min((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                .orElse(null);

        Booking nextBooking = approvedBookings.stream()
                .filter(b -> b.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);

        if (lastBooking != null) {
            itemDto.setLastBooking(BookingMapper.INSTANCE.toBookingDto(lastBooking));
        }
        if (nextBooking != null) {
            itemDto.setNextBooking(BookingMapper.INSTANCE.toBookingDto(nextBooking));
        }
    }

    private Item getItem(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item с id " + itemId + " не найден"));
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }

    private Booking getBooking(User booker, Item item) {
        return bookingRepository.findByBookerAndItem(booker, item)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + item.getId() + " не найдено для пользователя с id " + booker.getId()));
    }
}
