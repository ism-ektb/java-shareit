package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentInDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.OnCreateGroup;
import ru.practicum.shareit.user.dto.OnPatchGroup;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

/**
 * Контроллер создания и изменения и выдачи объектов класса Item
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient client;

    /**
     * Метод создания новой вещи
     *
     * @return возврашает объект с приваиным id
     * @throws ru.practicum.shareit.exception.ValidationException если пользователь пытается добавить (изменить) чужие объекты
     * @throws MethodArgumentNotValidException                    если принятый ItemDto не прошел валидацию
     */
    @PostMapping
    public ResponseEntity<Object> createItemDto(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @Validated(OnCreateGroup.class) @RequestBody ItemDto itemDto) {
        return client.createItem(itemDto, userId);
    }

    /**
     * Метод создания новой вещи
     *
     * @return возврашает объект с приваиным id
     * @throws ru.practicum.shareit.exception.ValidationException если пользователь пытается добавить (изменить) чужие объекты
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItemDto(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @Validated(OnPatchGroup.class) @RequestBody ItemDto itemDto,
                                                @PathVariable("id") long itemId) {
        return client.updateItem(userId, itemId, itemDto);
    }

    /**
     * Метод пролучения объекта из хранилища по id
     * возвращает ItemWithBookingAndCommentDto
     *
     * @param itemId id объекта класса Item
     * @throws ru.practicum.shareit.exception.ValidationException если объект с переданным id отсутствует в хранилище
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemDtoById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable("itemId") long itemId) {
        return client.getItemOfId(userId, itemId);
    }

    /**
     * Метод возвращает список объектов у которых поле Owner соответствет преданному параметру
     * метод возвращает List<ItemWithBookingAndCommentDto>
     *
     * @param userId id объекта класса User
     */
    @GetMapping()
    public ResponseEntity<Object> getItemDtoByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                                   @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        return client.getItems(userId, from, size);
    }

    /**
     * Метод возвращает список объектов (List<ItemDto>) из хранилища в поле name и description
     * которых встречается подстрока передаваемая в качестве параметра.
     * Если ничего не найдено, то возвращается пустой список
     */
    @GetMapping("/search")
    public ResponseEntity<Object> searchItemDtoByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @RequestParam(required = false, defaultValue = "") String text,
                                                      @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                                      @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        return client.getItemOfText(userId, text, from, size);
    }

    /**
     * Метод добавления комментария
     * возвращает CommentOutDto
     *
     * @throws MethodArgumentNotValidException при ошибке валидации commentInDto
     */
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addCommentForItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @PathVariable("itemId") long itemId,
                                                    @Valid @RequestBody CommentInDto commentInDto) {
        return client.addComment(userId, itemId, commentInDto);
    }
}
