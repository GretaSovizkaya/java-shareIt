package user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import user.dto.UserDto;
import user.model.User;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto toUserDto(User user);

    User toUser(UserDto userDto);
}
