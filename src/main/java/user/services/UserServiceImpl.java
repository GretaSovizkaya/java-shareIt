package user.services;

import exceptions.NotFoundException;
import exceptions.ValidatetionConflict;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import user.dto.UserDto;
import user.mapper.UserMapper;
import user.model.User;
import user.repository.UserRepository;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper; // Добавляем зависимость

    @Override
    public UserDto create(UserDto newUserDto) {
        if (userRepository.getUserByEmail(newUserDto.getEmail()).isPresent()) {
            throw new ValidatetionConflict("Пользователь с таким email уже зарегистрирован");
        }
        User user = userMapper.toUser(newUserDto); // Используем экземпляр маппера
        User createdUser = userRepository.create(user);
        return userMapper.toUserDto(createdUser); // Используем экземпляр маппера
    }

    @Override
    public UserDto update(long id, UserDto userUpdDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь " + id + " не найден"));

        if (userUpdDto.getEmail() != null && !userUpdDto.getEmail().equals(existingUser.getEmail())) {
            userRepository.getUserByEmail(userUpdDto.getEmail())
                    .ifPresent(user -> {
                        if (user.getId() != id) {
                            throw new ValidatetionConflict("Пользователь с таким email уже зарегистрирован");
                        }
                    });
        }

        User userToUpdate = userMapper.toUser(userUpdDto); // Используем экземпляр маппера
        User updatedUser = userRepository.update(id, userToUpdate);
        return userMapper.toUserDto(updatedUser); // Используем экземпляр маппера
    }

    @Override
    public void delete(long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Пользователь " + id + " не найден");
        }
        userRepository.delete(id);
    }

    @Override
    public UserDto findById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь " + id + " не найден"));
        return userMapper.toUserDto(user); // Используем экземпляр маппера
    }
}
