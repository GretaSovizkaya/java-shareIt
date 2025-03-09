package item.mapper;

import item.dto.CommentDto;
import item.model.Comment;
import item.model.Item;
import user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    // Преобразуем Comment в CommentDto
    @Mapping(target = "itemId", source = "item.id")  // Маппим itemId из объекта Item
    @Mapping(target = "authorName", source = "author.name")  // Маппим authorName из объекта User
    CommentDto toCommentDto(Comment comment);

    // Преобразуем CommentDto в Comment
    @Mapping(target = "item", source = "itemId")  // Маппим item из itemId
    @Mapping(target = "author", source = "authorName")  // Маппим author из authorName
    Comment toComment(CommentDto commentDto);

    // Добавляем методы для преобразования Long в Item и String в User
    default Item map(Long itemId) {
        Item item = new Item();
        item.setId(itemId);
        return item;
    }

    default User map(String authorName) {
        User user = new User();
        user.setName(authorName);
        return user;
    }
}
