package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.FormatDataException;
import ru.practicum.shareit.exception.NoFoundException;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dtoMapper.ItemListMapper;
import ru.practicum.shareit.item.dtoMapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.mapperDto.UserMapper;

import java.util.List;
import java.util.Optional;

/**
 * Сервисный класс для работы с объектами класса Item
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final ItemMapper itemMapper;
    private final ItemListMapper itemListMapper;
    private final ItemValidator itemValidator;
    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * Метод для добавления нового объекта в базу данных
     *
     * @param itemDto новый объект
     * @param userId  id зарегистрированного пользователя добавляющего объект
     * @throws FormatDataException если переданные в метод данные не соответствуют
     *                             "бизнес-логике"
     * @throws NoFoundException    если объект с userId не существует
     */
    @Override
    public ItemDto createItem(ItemDto itemDto, Optional<Long> userId) {
        itemValidator.checkCreateAndPatch(userId);
        Item item = itemMapper.dtoToModel(itemDto);
        User user = userMapper.dtoToModel(userService.findUserById(userId.get()));
        //полностью заполняем поле owner
        item.setOwner(user);
        return itemMapper.modelToDto(itemStorage.createItem(item));
    }

    /**
     * Метод для обновления данных об объекте в базе данных
     *
     * @param itemDto данные для обновления
     * @param itemId id обновляемого объекта
     * @param userId  id зарегистрированного пользователя обновляющего объект
     * @throws FormatDataException если переданные в метод данные не соответствуют
     *                             "бизнес-логике"
     * @throws NoFoundException если объект с userId не существует
     */
    @Override
    public ItemDto updateItem(Optional<Long> userId, long itemId, ItemDto itemDto) {
        itemValidator.checkCreateAndPatch(userId);
        Item item = itemMapper.dtoToModel(itemDto);
        User user = userMapper.dtoToModel(userService.findUserById(userId.get()));
        //полностью заполняем поле owner
        item.setOwner(user);
        return itemMapper.modelToDto(itemStorage.updateItem(itemId, item));
    }

    /**
     * Метод пролучения объекта из хранилища по id
     *
     * @param itemId id объекта класса Item
     * @throws NoFoundException если объект с переданным id отсутствует в хранилище
     * @throws FormatDataException если пользователь запрашивающий данные не зарегистрирован
     */
    @Override
    public ItemDto getItemOfId(Optional<Long> userId, long itemId) {
        itemValidator.checkGetRequest(userId);
        return itemMapper.modelToDto(itemStorage.getItemOfId(itemId));
    }

    /**
     * Метод возвращает список объектов у которых поле Owner соответствет преданному параметру
     *
     * @param userId id объекта класса User
     * @throws FormatDataException если пользователь запрашивающий данные не зарегистрирован
     */
    @Override
    public List<ItemDto> getItems(Optional<Long> userId) {
        itemValidator.checkGetRequest(userId);
        return itemListMapper.modelsToDtos(itemStorage.getItems(userId.get()));
    }

    /**
     * Метод возвращает список объектов из хранилища в поле name и description
     * которых встречается подстрока передаваемая в качестве параметра.
     * Если ничего не найдено, то возвращается пустой список
     * @throws FormatDataException если пользователь запрашивающий данные не зарегистрирован
     */
    @Override
    public List<ItemDto> getItemOfText(Optional<Long> userId, String text) {
        itemValidator.checkGetRequest(userId);
        return itemListMapper.modelsToDtos(itemStorage.getItemOfText(text));
    }
}
