package user.services;

import org.springframework.stereotype.Service;
import user.dto.UserDto;

import java.util.List;

@Service
public interface UserService {

    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto);

    void delete(long id);

    UserDto findById(long id);

    List<UserDto> getAll();
}