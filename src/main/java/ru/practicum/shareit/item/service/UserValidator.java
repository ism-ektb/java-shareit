package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.FormatDataException;

import java.util.Optional;

/**
 * Класс для проверки передаваемых объектов класса Item "бизнес-логике"
 */
@Component
@Slf4j
public class UserValidator {

    /**
     * Метод для проверки объектов класса ItemDto передаваемых в методы create() и update()
     * на сообветсвие "бизнес-логике"
     *
     * @throws FormatDataException если юзер добавляющий объекты не зарегистрирован
     *                             или пользователь пытается добавить (изменить) чужие объекты
     */
    public void checkCreateAndPatch(Optional<Long> userId) {
        if (userId.isEmpty()) {
            log.warn("Добавлять и обновлять вещи вещи могут только зарегистрированные юзеры");
            throw new FormatDataException("Добавлять и обновлять вещи могут только зарегистрированные юзеры");
        }
    }

    /**
     * Метод для проверки запросов на получение данных "бизнес-логике"
     *
     * @throws FormatDataException если пользователь запрашивающий данные не зарегистрирован
     */
    public void checkGetRequest(Optional userId) {
        if (userId.isEmpty()) {
            log.warn("Получать данные могут только зарегистрированные юзеры");
            throw new FormatDataException("Получать данные могут только зарегистрированные юзеры");
        }
    }


}
