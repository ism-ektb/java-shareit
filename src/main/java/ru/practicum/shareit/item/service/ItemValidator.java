package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.FormatDataException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dtoMapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.mapperDto.UserMapper;

import java.util.Optional;

/**
 * Класс для проверки передаваемых объектов класса Item "бизнес-логике"
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ItemValidator {
    private final ItemMapper itemMapper;
    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * Метод для проверки объектов класса ItemDto передаваемых в методы create() и update()
     * на сообветсвие "бизнес-логике"
     *
     * @return уже сконвертированный объект класса Item
     * @throws FormatDataException если юзер добавляющий объекты не зарегистрирован
     *                             или пользователь пытается добавить (изменить) чужие объекты
     */
    public Item checkCreateAndPatch(ItemDto itemDto, Optional<Long> userId) {
        if (userId.isEmpty()) {
            log.warn("Добавлять и обновлять вещи вещи могут только зарегистрированные юзеры");
            throw new FormatDataException("Добавлять и обновлять вещи могут только зарегистрированные юзеры");
        }
        Item item = itemMapper.dtoToModel(itemDto);
        User user = userMapper.dtoToModel(userService.findUserById(userId.get()));
        if (item.getOwner() == null) item.setOwner(user);
        else if (user.getId() != item.getOwner().getId()) {
            log.warn("Юзер с id: {} создавать и обновлять только о своей вещи");
            throw new FormatDataException("Юзер с id: " + userId.get()
                    + " может создавать и обновлять только о своей вещи");
            //если будет несоответсвие полей объектов Owner и User из памяти
        } else item.setOwner(user);
        return item;
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
