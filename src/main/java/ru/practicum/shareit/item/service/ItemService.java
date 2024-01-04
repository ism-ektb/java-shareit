package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.FormatDataException;
import ru.practicum.shareit.exception.NoFoundException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    /**
     * Метод для добавления нового объекта в базу данных
     *
     * @param itemDto новый объект
     * @param userId  id зарегистрированного пользователя добавляющего объект
     * @throws FormatDataException если переданные в метод данные не соответствуют
     *                             "бизнес-логике"
     */
    ItemDto createItem(ItemDto itemDto, Optional<Long> userId);

    /**
     * Метод для обновления данных об объекте в базе данных
     *
     * @param itemDto данные для обновления
     * @param itemId id обновляемого объекта
     * @param userId  id зарегистрированного пользователя обновляющего объект
     * @throws FormatDataException если переданные в метод данные не соответствуют
     *                             "бизнес-логике"
     */
    ItemDto updateItem(Optional<Long> userId, long itemId, ItemDto itemDto);

    /**
     * Метод пролучения объекта из хранилища по id
     *
     * @param itemId id объекта класса Item
     * @throws NoFoundException если объект с переданным id отсутствует в хранилище
     * @throws FormatDataException если пользователь запрашивающий данные не зарегистрирован
     */
    ItemDto getItemOfId(Optional<Long> userId, long itemId);

    /**
     * Метод возвращает список объектов у которых поле Owner соответствет преданному параметру
     *
     * @param userId id объекта класса User
     * @throws FormatDataException если пользователь запрашивающий данные не зарегистрирован
     */
    List<ItemDto> getItems(Optional<Long> userId);

    /**
     * Метод возвращает список объектов из хранилища в поле name и description
     * которых встречается подстрока передаваемая в качестве параметра.
     * Если ничего не найдено, то возвращается пустой список
     * @throws FormatDataException если пользователь запрашивающий данные не зарегистрирован
     */
    List<ItemDto> getItemOfText(Optional<Long> userId, String text);
}