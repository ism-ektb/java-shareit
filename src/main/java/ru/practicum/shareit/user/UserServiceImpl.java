package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.FormatDataException;
import ru.practicum.shareit.exception.NoFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapperDto.UserListMapper;
import ru.practicum.shareit.user.mapperDto.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final UserMapper userMapper;
    private final UserListMapper userListMapper;

    /**
     * запись в репозитория нового объекта класса User с присвоением нового id
     *
     * @throws FormatDataException если объект с таким емаил уже существует
     */
    @Override
    public UserDto createUser(UserDto userDto) {
        return userMapper.modelToDto(userStorage.createUser(
                userMapper.dtoToModel(userDto)));
    }

    /**
     * обновление данных объекта класса User
     *
     * @throws FormatDataException если объект с таким емаил уже существует
     * @throws NoFoundException    передан id не существующего объекта
     */
    @Override
    public UserDto updateUser(UserDto userDto, long id) {
        if ((userDto.getId() != null) && (userDto.getId() != id)) {
            log.warn("Юзер c id: {} не может изменить данные юзера: {}", id, userDto.toString());
            throw new FormatDataException("Юзер c id: "
                    + id + " не может изменить данные юзера: "
                    + userDto.toString());
        }
        return userMapper.modelToDto(userStorage.updateUser(
                userMapper.dtoToModel(userDto), id));
    }

    /**
     * получение списка всех юзеров
     */
    @Override
    public List<UserDto> findAllUsers() {
        return userListMapper.modelsToDtos(userStorage.getAllUsers());
    }

    /**
     * получить объект по переданному id
     *
     * @throws NoFoundException передан id не существующего объекта
     */
    @Override
    public UserDto findUserById(long id) {
        return userMapper.modelToDto(userStorage.getUser(id));
    }

    /**
     * удалить объект по переданному id
     *
     * @throws NoFoundException передан id не существующего объекта
     */
    @Override
    public void deleteUser(long id) {
        userStorage.deleteUser(id);
    }
}
