package user.mapper;

import org.mapstruct.Mapper;
import user.dto.UserDto;
import user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toUserDto(User user);

    User toUser(UserDto userDto);
}
