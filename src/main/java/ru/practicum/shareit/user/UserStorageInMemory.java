package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.FormatDataException;
import ru.practicum.shareit.exception.NoFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * имплементация репозитория сущности user
 */
@Slf4j
@Repository
public class UserStorageInMemory implements UserStorage {

    private Map<Long, User> userMap = new HashMap<>();
    private Long nextId = 1L;

    /**
     * запись в репозитория нового объекта класса User с присвоением нового id
     *
     * @throws FormatDataException если объект с таким емаил уже существует
     */
    @Override
    public User createUser(User user) {
        // проверка на уникальность
        if (userMap.containsValue(user)) {
            log.warn("Уникальные данные User: {} повторяются", user.toString());
            throw new FormatDataException("Уникальные данные User " + user.toString() + " повторяются");
        }
        user.setId(nextId++);
        userMap.put(user.getId(), user);

        return getUser(user.getId());
    }

    /**
     * обновление данных объекта класса User
     *
     * @throws FormatDataException если объект с таким емаил уже существует
     * @throws NoFoundException    передан id не существующего объекта
     */
    @Override
    public User updateUser(User user, long id) {
        //валидация переданных в метод данных
        if (!(userMap.containsKey(id))) {
            log.warn("User c id: {} не существует", id);
            throw new NoFoundException("User c id: " + id + " не существует");
        }
        if (user.getEmail() != null) {
            if (userMap.containsValue(user) && (!userMap.get(id).equals(user))) {
                log.warn("Уникальные данные User: {} повторяются", user.toString());
                throw new FormatDataException("Уникальные данные User " + user.toString() + " повторяются");
            }
        }

        User newUser = User.builder()
                .id(id)
                .name(user.getName() == null ? userMap.get(id).getName() : user.getName())
                .email(user.getEmail() == null ? userMap.get(id).getEmail() : user.getEmail())
                .build();
        userMap.replace(id, newUser);
        return newUser;
    }

    /**
     * получение списка всех юзеров
     */
    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    /**
     * получить объект по переданному id
     * @throws NoFoundException  передан id не существующего объекта
     */
    @Override
    public User getUser(long id) {
        if (!(userMap.containsKey(id))) {
            log.warn("пользователь с id: {} не существует", id);
            throw new NoFoundException("пользователь с id: " + id + " не существует");
        }
        return userMap.get(id);
    }

    /**
     * удалить объект по переданному id
     * @throws NoFoundException  передан id не существующего объекта
     */
    @Override
    public void deleteUser(long id) {
        if (!(userMap.containsKey(id))) {
            log.warn("пользователь с id: {} не существует", id);
            throw new NoFoundException("пользователь с id: " + id + " не существует");
        } else
            userMap.remove(id);
    }
}
