package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.NoFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsForThisReqDto;

import java.util.List;

public interface ItemRequestService {

    /**
     * Метод для сознания запроса
     *
     * @throws NoFoundException если пользователь с userId не существует
     */
    ItemRequestDto create(long userId, ItemRequestDto itemRequestDto);

    /**
     * Метод возвращает список запросов созданных Юзезом.
     * К каждому запросу прикреплен список вещей созданных под этот запрос
     *
     * @throws NoFoundException если пользователь с userId не существует
     */
    List<ItemRequestWithItemsForThisReqDto>
    getReqUserWithItemsForThisReq(long userId, PageRequest pageRequest);

    /**
     * Метод возвращает список запросов созданных другими Юзерами.
     * К каждому запросу прикреплен список вещей созданных под этот запрос
     *
     * @throws NoFoundException если пользователь с userId не существует
     */
    List<ItemRequestWithItemsForThisReqDto>
    getReqAllWithItemsForThisReq(long userId, PageRequest pageRequest);

    /**
     * Метод возвращает запрос по его Id.
     * К запросу прикреплен список вещей созданных под этот запрос
     *
     * @throws NoFoundException если пользователь с userId или запрос с requestId не существует
     */
    ItemRequestWithItemsForThisReqDto getReqById(long iserId, long requestId);
}
