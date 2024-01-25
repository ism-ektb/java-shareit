package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentInDto;
import ru.practicum.shareit.comment.dto.CommentOutDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentDto;
import ru.practicum.shareit.item.dto.OnCreateGroup;
import ru.practicum.shareit.item.dto.OnPatchGroup;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * Контроллер создания и изменения и выдачи объектов класса Item
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemService itemService;

    /**
     * Метод создания новой вещи
     *
     * @return возврашает объект с приваиным id
     * @throws ru.practicum.shareit.exception.ValidationException если пользователь пытается добавить (изменить) чужие объекты
     * @throws MethodArgumentNotValidException если принятый ItemDto не прошел валидацию
     */
    @PostMapping
    public ItemDto createItemDto(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @Validated(OnCreateGroup.class) @RequestBody ItemDto itemDto) {
        return itemService.createItem(itemDto, userId);
    }

    /**
     * Метод создания новой вещи
     *
     * @return возврашает объект с приваиным id
     * @throws ru.practicum.shareit.exception.ValidationException если пользователь пытается добавить (изменить) чужие объекты
     */
    @PatchMapping("/{id}")
    public ItemDto updateItemDto(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @Validated(OnPatchGroup.class) @RequestBody ItemDto itemDto,
                                 @PathVariable("id") long itemId) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    /**
     * Метод пролучения объекта из хранилища по id
     *
     * @param itemId id объекта класса Item
     * @throws ru.practicum.shareit.exception.ValidationException если объект с переданным id отсутствует в хранилище
     */
    @GetMapping("/{itemId}")
    public ItemWithBookingAndCommentDto getItemDtoById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                       @PathVariable("itemId") long itemId) {
        return itemService.getItemOfId(userId, itemId);
    }

    /**
     * Метод возвращает список объектов у которых поле Owner соответствет преданному параметру
     *
     * @param userId id объекта класса User
     */
    @GetMapping()
    public List<ItemWithBookingAndCommentDto> getItemDtoByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                               @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                                               @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        return itemService.getItems(userId, PageRequest.of(from / size, size));
    }

    /**
     * Метод возвращает список объектов из хранилища в поле name и description
     * которых встречается подстрока передаваемая в качестве параметра.
     * Если ничего не найдено, то возвращается пустой список
     */
    @GetMapping("/search")
    public List<ItemDto> searchItemDtoByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestParam(required = false, defaultValue = "") String text,
                                             @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                             @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        return itemService.getItemOfText(userId, text, PageRequest.of(from / size, size));
    }

    /**
     * Метод добавления комментария
     *
     * @throws MethodArgumentNotValidException при ошибке валидации commentInDto
     */
    @PostMapping("/{itemId}/comment")
    public CommentOutDto addCommentForItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PathVariable("itemId") long itemId,
                                           @Valid @RequestBody CommentInDto commentInDto) {
        return itemService.addComment(userId, itemId, commentInDto);
    }
}
