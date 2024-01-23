package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsForThisReqDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestService service;

    /**
     * Метод создания запроса
     *
     * @throws MethodArgumentNotValidException если аргументы не прошли валидацию
     * @throws MissingRequestHeaderException   если отсутствует аргумент в заголовке
     */
    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return service.create(userId, itemRequestDto);
    }

    /**
     * Метод возвращает список запросов созданных Юзезом.
     * К каждому запросу прикреплен список вещей созданных под этот запрос
     *
     * @throws MethodArgumentNotValidException если аргументы не прошли валидацию
     * @throws MissingRequestHeaderException   если отсутствует аргумент в заголовке
     */
    @GetMapping
    public List<ItemRequestWithItemsForThisReqDto>
    getUserRequestsWithItem(@RequestHeader("X-Sharer-User-Id") long userId,
                            @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                            @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        return service.getReqUserWithItemsForThisReq(userId, PageRequest.of(from / size, size));
    }

    /**
     * Метод возвращает список запросов созданных другими Юзерами.
     * К каждому запросу прикреплен список вещей созданных под этот запрос
     *
     * @throws MethodArgumentNotValidException если аргументы не прошли валидацию
     * @throws MissingRequestHeaderException   если отсутствует аргумент в заголовке
     */
    @GetMapping("/all")
    public List<ItemRequestWithItemsForThisReqDto>
    getAllRequestsWithItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                           @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        return service.getReqAllWithItemsForThisReq(userId, PageRequest.of(from / size, size));
    }

    /**
     * Метод возвращает запрос по его Id.
     * К запросу прикреплен список вещей созданных под этот запрос
     *
     * @throws MethodArgumentNotValidException если аргументы не прошли валидацию
     * @throws MissingRequestHeaderException   если отсутствует аргумент в заголовке
     */
    @GetMapping("/{requestId}")
    public ItemRequestWithItemsForThisReqDto
    getRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                   @PathVariable("requestId") @Positive long requestId) {
        return service.getReqById(userId, requestId);
    }
}
