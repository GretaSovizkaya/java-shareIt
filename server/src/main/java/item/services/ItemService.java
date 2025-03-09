package item.services;

import item.dto.CommentDto;
import item.dto.CommentInfoDto;
import item.dto.ItemDto;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public interface ItemService {
    ItemDto create(ItemDto item, long ownerId);

    ItemDto update(ItemDto itemUpd, long ownerId);

    void delete(long id);

    ItemDto findById(long id, long userId);

    Collection<ItemDto> findByOwner(long ownerId);

    Collection<ItemDto> findBySearch(String text);

    CommentDto createComment(long itemId, long userId, CommentInfoDto commentDto);
}