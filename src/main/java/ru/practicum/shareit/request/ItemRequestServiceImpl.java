package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NoFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dtoMapper.ItemListMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsForThisReqDto;
import ru.practicum.shareit.request.mapperDto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository repository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper mapper;
    private final ItemListMapper itemListMapper;
    private final UserService userService;

    /**
     * Метод для сознания запроса
     *
     * @throws NoFoundException если пользователь с userId не существует
     */
    @Override
    public ItemRequestDto create(long userId, ItemRequestDto itemRequestDto) {
        User user = userService.findUserByIdForValid(userId);
        ItemRequest itemRequest = mapper.dtoToModel(itemRequestDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);
        ItemRequest itemRequestAfterSave = repository.save(itemRequest);
        return mapper.modelToDto(itemRequestAfterSave);
    }

    /**
     * Метод возвращает список запросов созданных Юзезом.
     * К каждому запросу прикреплен список вещей созданных под этот запрос
     *
     * @throws NoFoundException если пользователь с userId не существует
     */
    @Override
    public List<ItemRequestWithItemsForThisReqDto> getReqUserWithItemsForThisReq(long userId, PageRequest pageRequest) {
        userService.findUserByIdForValid(userId);
        List<ItemRequest> itemRequest = repository.findAllByRequestorIdOrderByCreatedDesc(userId, pageRequest);
        return itemRequest.stream()
                .map(i -> {
                    List<Item> items = itemRepository.findAllByRequestId(i.getId());
                    ItemRequestWithItemsForThisReqDto itemRequestWithItemsForThisReqDto
                            = mapper.modelToDtoWithListOfItem(i);
                    itemRequestWithItemsForThisReqDto
                            .setItems(itemListMapper.modelsToDtosForRequest(items));
                    return itemRequestWithItemsForThisReqDto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Метод возвращает список запросов созданных другими Юзерами.
     * К каждому запросу прикреплен список вещей созданных под этот запрос
     *
     * @throws NoFoundException если пользователь с userId не существует
     */
    @Override
    public List<ItemRequestWithItemsForThisReqDto> getReqAllWithItemsForThisReq(long userId, PageRequest pageRequest) {
        userService.findUserByIdForValid(userId);
        List<ItemRequest> itemRequest = repository.findAllByRequestorIdIsNotOrderByCreatedDesc(userId, pageRequest);
        return itemRequest.stream()
                .map(i -> {
                    List<Item> items = itemRepository.findAllByRequestId(i.getId());
                    ItemRequestWithItemsForThisReqDto itemRequestWithItemsForThisReqDto
                            = mapper.modelToDtoWithListOfItem(i);
                    itemRequestWithItemsForThisReqDto
                            .setItems(itemListMapper.modelsToDtosForRequest(items));
                    return itemRequestWithItemsForThisReqDto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Метод возвращает запрос по его Id.
     * К запросу прикреплен список вещей созданных под этот запрос
     *
     * @throws NoFoundException если пользователь с userId или запрос с requestId не существует
     */
    @Override
    public ItemRequestWithItemsForThisReqDto getReqById(long userId, long requestId) {
        userService.findUserByIdForValid(userId);
        ItemRequest itemRequest = repository.findById(requestId).orElseThrow(() -> {
            log.warn("Запрос на вещь с id {} не найден", requestId);
            throw new NoFoundException("Запрос на вещь с id " + requestId + " не найден");
        });
        ItemRequestWithItemsForThisReqDto itemRequestWithItemsForThisReqDto =
                mapper.modelToDtoWithListOfItem(itemRequest);
        List<Item> items = itemRepository.findAllByRequestId(requestId);
        itemRequestWithItemsForThisReqDto
                .setItems(itemListMapper.modelsToDtosForRequest(items));
        return itemRequestWithItemsForThisReqDto;
    }
}
