package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.FormatDataException;
import ru.practicum.shareit.exception.NoFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    /**
     * запись в репозитория нового объекта класса User с присвоением нового id
     *
     * @throws FormatDataException если объект с таким емаил уже существует
     */
    UserDto createUser(UserDto userDto);

    /**
     * обновление данных объекта класса User
     *
     * @throws FormatDataException если объект с таким емаил уже существует
     * @throws NoFoundException    передан id не существующего объекта
     */
    UserDto updateUser(UserDto userDto, long id);

    /**
     * получение списка всех юзеров
     */
    List<UserDto> findAllUsers();

    /**
     * получить объект по переданному id
     *
     * @throws NoFoundException передан id не существующего объекта
     */
    UserDto findUserById(long id);

    /**
     * удалить объект по переданному id
     *
     * @throws NoFoundException передан id не существующего объекта
     */
    void deleteUser(long id);
}
