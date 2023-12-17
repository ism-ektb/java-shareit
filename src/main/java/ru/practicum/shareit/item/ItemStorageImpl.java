package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NoFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * класс для хранения объектов класса Item в памяти
 */
@Repository
@Slf4j
public class ItemStorageImpl implements ItemStorage {



    Map<Long, Item> itemMap = new HashMap<>();
    long nextId = 1L;

    /**
     * Метод сохранение нового объекта с присваиванием ему id
     *
     * @param item сохраняемый объект (без id)
     * @return сохпвненный объект, которому присвоен id
     */
    @Override
    public Item createItem(Item item) {
        item.setId(nextId++);
        itemMap.put(item.getId(), item);
        return item;
    }

    /**
     * Метод пролучения объекта из хранилища по id
     *
     * @param itemId id объекта класса Item
     * @throws NoFoundException если объект с переданным id отсутствует в хранилище
     */
    @Override
    public Item getItemOfId(long itemId) {
        if (!(itemMap.containsKey(itemId))) {
            log.warn("Вещь с id: {} отсутствует", itemId);
            throw new NoFoundException("Вешь с id: " + itemId + " отсутствует");
        }
        return itemMap.get(itemId);
    }

    /**
     * метод частично или полностью обновляет поля хранящегося объекта, кроме поля Owner.
     *
     * @param itemId id изменяемого объекта
     * @param item   новые значения полей объекта, если поле равно null, то обновление не происходит
     * @return обновленный объект.
     * @throws NoFoundException если объект с переданным id отсутствует или при попытке изменить поле
     *                          Owner
     */
    @Override
    public Item updateItem(long itemId, Item item) {
        if (!(itemMap.containsKey(itemId))) {
            log.warn("Вещь с id: {} отсутствует", itemId);
            throw new NoFoundException("Вешь с id: " + itemId + " отсутствует");
        }
        if (!(item.getOwner().equals(itemMap.get(itemId).getOwner()))) {
            log.warn("Для смены хозяина с {} на {} Вещи c id {} используйте другой метод",
                    itemMap.get(itemId).getOwner(),
                    item.getOwner(),
                    itemId);
            throw new NoFoundException("Для смены хозяина с "
                    + itemMap.get(itemId).getOwner() + " на "
                    + " Вещи c id " + itemId + " используйте другой метод");
        }

        Item newItem = Item.builder()
                .id(itemId)
                .name(item.getName() == null ? itemMap.get(itemId).getName() : item.getName())
                .description(item.getDescription() == null ?
                        itemMap.get(itemId).getDescription() : item.getDescription())
                .owner(itemMap.get(itemId).getOwner())
                .available(item.getAvailable() == null ?
                        itemMap.get(itemId).getAvailable() : item.getAvailable())
                .request(item.getRequest() == null ?
                        itemMap.get(itemId).getRequest() : item.getRequest())
                .build();

        itemMap.replace(itemId, newItem);
        return newItem;
    }

    /**
     * Метод возвращает список объектов у которых поле Owner соответствет преданному параметру
     * либо список всех объектов если передан null
     *
     * @param userId id объекта класса User
     */
    @Override
    public List<Item> getItems(Long userId) {
        if (userId == null) return new ArrayList<>(itemMap.values());
        return itemMap.values().stream()
                .filter(i -> i.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    /**
     * Метод возвращает список объектов из хранилища в поле name и description
     * которых встречается подстрока передаваемая в качестве параметра.
     * Если ничего не найдено, то возвращается пустой список
     */
    @Override
    public List<Item> getItemOfText(String text) {
        if (text.isBlank()) return new ArrayList<>();
        String str = text.toLowerCase();
        return itemMap.values().stream()
                .filter(i -> (i.getAvailable()
                        && ((i.getName().toLowerCase().indexOf(str) != -1)
                        || (i.getDescription().toLowerCase().indexOf(str) != -1))))
                .collect(Collectors.toList());
    }
}
