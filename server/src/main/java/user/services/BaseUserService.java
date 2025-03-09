package user.services;

import exceptions.NotFoundException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import user.dto.UserDto;
import user.mapper.UserMapper;
import user.model.User;
import user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BaseUserService implements UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public UserDto update(UserDto userDto) {
        User oldUser = getUser(userDto.getId());
        if (userDto.getEmail() != null && !userDto.getEmail().equals(oldUser.getEmail())) {
            oldUser.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null && !userDto.getName().equals(oldUser.getName())) {
            oldUser.setName(userDto.getName());
        }
        return userMapper.toUserDto(userRepository.save(oldUser));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findById(long id) {
        return userMapper.toUserDto(getUser(id));
    }

    @Transactional
    @Override
    public void delete(long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + userId));
    }
}