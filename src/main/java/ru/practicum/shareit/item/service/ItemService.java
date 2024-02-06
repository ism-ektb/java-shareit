package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.comment.dto.CommentInDto;
import ru.practicum.shareit.comment.dto.CommentOutDto;
import ru.practicum.shareit.exception.NoFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentDto;

import java.util.List;

public interface ItemService {
    /**
     * Метод для добавления нового объекта в базу данных
     *
     * @param itemDto новый объект
     * @param userId  id зарегистрированного пользователя добавляющего объект
     */
    ItemDto createItem(ItemDto itemDto, long userId);

    /**
     * Метод для обновления данных об объекте в базе данных
     *
     * @param itemDto данные для обновления
     * @param itemId  id обновляемого объекта
     * @param userId  id зарегистрированного пользователя обновляющего объект
     */
    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    /**
     * Метод пролучения объекта из хранилища по id
     *
     * @param itemId id объекта класса Item
     * @throws NoFoundException если объект с переданным id отсутствует в хранилище
     */
    ItemWithBookingAndCommentDto getItemOfId(long userId, long itemId);

    /**
     * Метод возвращает список объектов у которых поле Owner соответствет преданному параметру
     *
     * @param userId id объекта класса User
     */
    List<ItemWithBookingAndCommentDto> getItems(long userId, Pageable pageRequest);

    /**
     * Метод возвращает список объектов из хранилища в поле name и description
     * которых встречается подстрока передаваемая в качестве параметра.
     * Если ничего не найдено, то возвращается пустой список
     */
    List<ItemDto> getItemOfText(long userId, String text, Pageable pageRequest);

    CommentOutDto addComment(long userId, long itemId, CommentInDto commentInDto);
}