package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

/**
 * Controller item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestClient client;

    /**
     * Метод создания запроса
     * возвращает ItemRequestDto
     *
     * @throws MethodArgumentNotValidException если аргументы не прошли валидацию
     * @throws MissingRequestHeaderException   если отсутствует аргумент в заголовке
     */
    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return client.create(userId, itemRequestDto);
    }

    /**
     * Метод возвращает список запросов созданных Юзезом (List<ItemRequestWithItemsForThisReqDto>).
     * К каждому запросу прикреплен список вещей созданных под этот запрос
     *
     * @throws MethodArgumentNotValidException если аргументы не прошли валидацию
     * @throws MissingRequestHeaderException   если отсутствует аргумент в заголовке
     */
    @GetMapping
    public ResponseEntity<Object>
    getUserRequestsWithItem(@RequestHeader("X-Sharer-User-Id") long userId,
                            @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                            @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        return client.getReqUserWithItemsForThisReq(userId, from, size);
    }

    /**
     * Метод возвращает список запросов созданных другими Юзерами (List<ItemRequestWithItemsForThisReqDto>).
     * К каждому запросу прикреплен список вещей созданных под этот запрос
     *
     * @throws MethodArgumentNotValidException если аргументы не прошли валидацию
     * @throws MissingRequestHeaderException   если отсутствует аргумент в заголовке
     */
    @GetMapping("/all")
    public ResponseEntity<Object>
    getAllRequestsWithItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                           @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        return client.getReqAllWithItemsForThisReq(userId, from, size);
    }

    /**
     * Метод возвращает (ItemRequestWithItemsForThisReqDto) запрос по его Id.
     * К запросу прикреплен список вещей созданных под этот запрос
     *
     * @throws MethodArgumentNotValidException если аргументы не прошли валидацию
     * @throws MissingRequestHeaderException   если отсутствует аргумент в заголовке
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<Object>
    getRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                   @PathVariable("requestId") @Positive long requestId) {
        return client.getReqById(userId, requestId);
    }
}
