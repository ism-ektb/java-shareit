package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NoFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapperDto.UserListMapper;
import ru.practicum.shareit.user.mapperDto.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserListMapper userListMapper;
    private final UserRepository repository;

    /**
     * запись в репозитория нового объекта класса User с присвоением нового id
     *
     * @throws java.sql.SQLException если email не уникальный
     */
    @Override
    public UserDto createUser(UserDto userDto) {
        User newUser = userMapper.dtoToModel(userDto);
        return userMapper.modelToDto(repository.save(newUser));
    }

    /**
     * обновление данных объекта класса User
     *
     * @throws java.sql.SQLException если объект с таким емаил уже существует
     * @throws NoFoundException      передан id не существующего объекта
     */
    @Override
    public UserDto updateUser(UserDto userDto, long id) {
        if ((userDto.getId() != null) && (userDto.getId() != id)) {
            log.warn("Юзер c id: {} не может изменить данные юзера: {}", id, userDto.toString());
            throw new ValidationException("Юзер c id: "
                    + id + " не может изменить данные юзера: "
                    + userDto.toString());
        }
        User oldUser = findUserByIdForValid(id);
        User newUser = userMapper.dtoToModel(userDto);

        User user = repository.save(
                User.builder()
                        .id(id)
                        .name(newUser.getName() == null ? oldUser.getName() : newUser.getName())
                        .email(newUser.getEmail() == null ? oldUser.getEmail() : newUser.getEmail())
                        .build());
        return userMapper.modelToDto(user);
    }

    /**
     * получение списка всех юзеров
     */
    @Override
    public List<UserDto> findAllUsers() {
        return userListMapper.modelsToDtos(repository.findAll());
    }

    /**
     * получить объект по переданному id
     *
     * @throws NoFoundException передан id не существующего объекта
     */
    @Override
    public UserDto findUserById(long id) {
        return userMapper.modelToDto(repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User c id: {} не существует", id);
                    throw new NoFoundException("User c id: " + id + " не существует");
                }));
    }

    /**
     * удалить объект по переданному id
     *
     * @throws NoFoundException передан id не существующего объекта
     */
    @Override
    public void deleteUser(long id) {
        repository.deleteById(id);
    }

    @Override
    public User findUserByIdForValid(long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User c id: {} не существует", id);
                    throw new NoFoundException("User c id: " + id + " не существует");
                });
    }
}
