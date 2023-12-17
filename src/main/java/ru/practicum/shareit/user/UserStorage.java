package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.FormatDataException;
import ru.practicum.shareit.exception.NoFoundException;

import java.util.List;

/**
 * Интерфейс для хранения экземпляров класса User
 */
@Component
public interface UserStorage {

    /**
     * запись в репозитория нового объекта класса User с присвоением нового id
     *
     * @throws FormatDataException если объект с таким емаил уже существует
     */
    User createUser(User user);

    /**
     * обновление данных объекта класса User
     *
     * @throws FormatDataException если объект с таким емаил уже существует
     * @throws NoFoundException    передан id не существующего объекта
     */
    User updateUser(User user, long id);

    /**
     * получение списка всех юзеров
     */
    List<User> getAllUsers();

    /**
     * получить объект по переданному id
     *
     * @throws NoFoundException передан id не существующего объекта
     */
    User getUser(long id);

    /**
     * удалить объект по переданному id
     *
     * @throws NoFoundException передан id не существующего объекта
     */
    void deleteUser(long id);
}
