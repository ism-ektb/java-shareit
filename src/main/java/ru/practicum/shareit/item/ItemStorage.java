package ru.practicum.shareit.item;

import ru.practicum.shareit.exception.NoFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * интерфейс для хранения объектов класса Item
 */
public interface ItemStorage {
    /**
     * Метод сохранение нового объекта с присваиванием ему id
     *
     * @param item сохраняемый объект (без id)
     * @return сохпвненный объект, которому присвоен id
     */
    Item createItem(Item item);

    /**
     * Метод пролучения объекта из хранилища по id
     *
     * @param itemId id объекта класса Item
     * @throws NoFoundException если объект с переданным id отсутствует в хранилище
     */
    Item getItemOfId(long itemId);

    /**
     * метод частично или полностью обновляет поля хранящегося объекта, кроме поля Owner.
     *
     * @param itemId id изменяемого объекта
     * @param item   новые значения полей объекта, если поле равно null, то обновление не происходит
     * @return обновленный объект.
     * @throws NoFoundException если объект с переданным id отсутствует или при попытке изменить поле
     *                          Owner
     */
    Item updateItem(long itemId, Item item);

    /**
     * Метод возвращает список объектов у которых поле Owner соответствет преланному параметру
     * либо список всех объектов если передан null
     *
     * @param userId id объекта класса User
     */
    List<Item> getItems(Long userId);

    /**
     * Метод возвращает список объектов из хранилища в поле name и description
     * которых встречается подстрока передаваемая в качестве параметра.
     * Если ничего не найдено, то возвращается пустой список
     */
    List<Item> getItemOfText(String text);
}