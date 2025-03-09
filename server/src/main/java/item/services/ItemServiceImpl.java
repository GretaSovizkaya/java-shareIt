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
import request.model.ItemRequest;
import request.repository.ItemRequestRepository;
import user.model.User;
import user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class ItemServiceImpl implements  ItemService {
    ItemRepository itemRepository;
    UserRepository userRepository;
    BookingRepository bookingRepository;
    CommentRepository commentRepository;
    ItemRequestRepository itemRequestRepository;
    ItemMapper itemMapper;
    BookingMapper bookingMapper;
    CommentMapper commentMapper;


    @Override
    @Transactional
    public ItemDto create(ItemDto newItemDto, long ownerId) {
        User owner = getUser(ownerId);

        Item item = itemMapper.toItem(newItemDto);
        item.setOwner(owner);

        if (newItemDto.getRequestId() != null) {
            item.setItemRequest(getRequest(newItemDto.getRequestId()));
        }

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
        return itemMapper.toItemDto(itemRepository.save(itemRepository.save(oldItem)));
    }

    @Override
    @Transactional
    public void delete(long itemId) {
        itemRepository.delete(itemRepository.findById(itemId).get());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto findById(long itemId, long userId) {
        Item item = getItem(itemId);
        ItemDto itemDto = itemMapper.toItemDto(item);

        if (item.getOwner().getId().equals(userId)) {
            setLastNextBooking(itemDto);
        }
        List<CommentDto> comments = commentRepository.findByItem(item).stream()
                .map(commentMapper::toCommentDto).toList();
        itemDto.setComments(comments);
        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> findByOwner(long ownerId) {
        User owner = getUser(ownerId);
        userRepository.findById(ownerId);
        List<ItemDto> itemsDto = itemRepository.findByOwner(owner)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
        for (ItemDto iDto : itemsDto) {

            setLastNextBooking(iDto);
            List<CommentDto> comments = commentRepository.findByItem(getItem(iDto.getId())).stream()
                    .map(commentMapper::toCommentDto).toList();
            iDto.setComments(comments);
        }
        return itemsDto;
    }

    @Override
    @Transactional
    public Collection<ItemDto> findBySearch(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.searchByNameOrDescription(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Transactional
    @Override
    public CommentDto createComment(long itemId, long userId, CommentInfoDto commentDto) {
        Item item = getItem(itemId);
        User user = getUser(userId);
        Booking booking = getBooking(user, item);
        if (!booking.getStatus().equals(BookingStatus.APPROVED) || booking.getEnd().isAfter(LocalDateTime.now())) {
            throw new ValidationException("Бронирование вещи с id: не завершено" + itemId);
        }
        Comment comment = Comment.builder()
                .text(commentDto.getText())
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();

        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    private void setLastNextBooking(ItemDto itemDto) {
        LocalDateTime toDay = LocalDateTime.now();
        List<Booking> listApproveBooking = bookingRepository.findApprovedForItem(getItem(itemDto.getId()), Sort.by(Sort.Direction.DESC, "start"));
        Booking lastBooking = listApproveBooking.stream()
                .filter(b -> b.getStart().isBefore(toDay))
                .min((b1, b2) -> b2.getStart().compareTo(b1.getStart())).orElse(null);
        Booking nextBooking = listApproveBooking.stream()
                .filter(b -> b.getStart().isAfter(toDay))
                .min(Comparator.comparing(Booking::getStart)).orElse(null);
        if (lastBooking != null) {
            itemDto.setLastBooking(bookingMapper.toBookingDto(lastBooking));
        }
        if (nextBooking != null) {
            itemDto.setNextBooking(bookingMapper.toBookingDto(nextBooking));
        }
    }

    private Item getItem(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id: не найден: " + itemId));
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: не найден: " + userId));
    }

    private ItemRequest getRequest(long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос на вещь с id: не найден: " + requestId));
    }

    private Booking getBooking(User booker, Item item) {
        return bookingRepository.findByBookerAndItem(booker, item)
                .orElseThrow(() -> new NotFoundException("Бронирование вещи с id: " + item.getId() +
                        "с пользователем с id: " + booker.getId() + " not found"));

    }
}