package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.FormatDataException;
import ru.practicum.shareit.exception.NoFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OnCreateGroup;
import ru.practicum.shareit.item.dto.OnPatchGroup;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import java.util.Optional;

/**
 * Контроллер создания и изменения и выдачи объектов класса Item
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    /**
     * Метод создания новой вещи
     *
     * @return возврашает объект с приваиным id
     * @throws FormatDataException             если юзер добавляющий объекты не зарегистрирован
     *                                         или пользователь пытается добавить (изменить) чужие объекты
     * @throws MethodArgumentNotValidException если принятый ItemDto не прошел валидацию
     */
    @PostMapping
    public ItemDto createItemDto(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                 @Validated(OnCreateGroup.class) @RequestBody ItemDto itemDto) {
        return itemService.createItem(itemDto, userId);
    }

    /**
     * Метод создания новой вещи
     *
     * @return возврашает объект с приваиным id
     * @throws FormatDataException если юзер добавляющий объекты не зарегистрирован
     *                             или пользователь пытается добавить (изменить) чужие объекты
     */
    @PatchMapping("/{id}")
    public ItemDto updateItemDto(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                 @Validated(OnPatchGroup.class) @RequestBody ItemDto itemDto,
                                 @PathVariable("id") long itemId) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    /**
     * Метод пролучения объекта из хранилища по id
     *
     * @param itemId id объекта класса Item
     * @throws NoFoundException    если объект с переданным id отсутствует в хранилище
     * @throws FormatDataException если пользователь запрашивающий данные не зарегистрирован
     */
    @GetMapping("/{itemId}")
    public ItemDto getItemDtoById(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                  @PathVariable("itemId") long itemId) {
        return itemService.getItemOfId(userId, itemId);
    }

    /**
     * Метод возвращает список объектов у которых поле Owner соответствет преданному параметру
     *
     * @param userId id объекта класса User
     * @throws FormatDataException если пользователь запрашивающий данные не зарегистрирован
     */
    @GetMapping()
    public List<ItemDto> getItemDtoByUser(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId) {
        return itemService.getItems(userId);
    }

    /**
     * Метод возвращает список объектов из хранилища в поле name и description
     * которых встречается подстрока передаваемая в качестве параметра.
     * Если ничего не найдено, то возвращается пустой список
     *
     * @throws FormatDataException если пользователь запрашивающий данные не зарегистрирован
     */
    @GetMapping("/search")
    public List<ItemDto> getItemDtoByUser(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                          @RequestParam("text") String text) {
        return itemService.getItemOfText(userId, text);
    }
}
