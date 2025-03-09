package item.mapper;

import item.dto.CommentDto;
import item.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(target = "itemId", source = "item.id")
    @Mapping(target = "authorName", source = "author.name")
    CommentDto toCommentDto(Comment comment);

    @Mapping(target = "item", ignore = true) // item связывается в сервисе
    @Mapping(target = "author", ignore = true) // author связывается в сервисе
    Comment toComment(CommentDto commentDto);
}
