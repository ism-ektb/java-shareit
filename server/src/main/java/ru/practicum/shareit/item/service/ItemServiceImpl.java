package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentInDto;
import ru.practicum.shareit.comment.dto.CommentOutDto;
import ru.practicum.shareit.comment.mapper.CommentListMapper;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.exception.NoFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentDto;
import ru.practicum.shareit.item.dtoMapper.ItemListMapper;
import ru.practicum.shareit.item.dtoMapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.mapperDto.UserMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервисный класс для работы с объектами класса Item
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;
    private final ItemListMapper itemListMapper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final ItemRepository repository;
    private final BookingService bookingService;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final CommentListMapper commentListMapper;


    /**
     * Метод для добавления нового объекта в базу данных
     *
     * @param itemDto новый объект
     * @param userId  id зарегистрированного пользователя добавляющего объект
     * @throws NoFoundException если объект с userId не существует
     */
    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        Item item = itemMapper.dtoToModel(itemDto);
        //проверяем userId
        User user = userService.findUserByIdForValid(userId);
        //полностью заполняем поле owner
        item.setOwner(user);
        return itemMapper.modelToDto(repository.save(item));
    }

    /**
     * Метод для обновления данных об объекте в базе данных
     *
     * @param itemDto данные для обновления
     * @param itemId  id обновляемого объекта
     * @param userId  id зарегистрированного пользователя обновляющего объект
     * @throws NoFoundException если объект с userId не существует
     */
    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        Item newItem = itemMapper.dtoToModel(itemDto);
        //неявно проверяем что userId валидно
        userService.findUserByIdForValid(userId);
        Item oldItem = repository.findById(itemId).orElseThrow(() -> {
            log.warn("Вещь с id: {} отсутствует", itemId);
            throw new NoFoundException("Вешь с id: " + itemId + " отсутствует");
        });
        if (oldItem.getOwner().getId() != userId) {
            log.warn("только хозяин может редактировать данные о вещи {}", oldItem.toString());
            throw new ValidationException("только хозяин может редактировать данные о вещи " + oldItem.toString());
        }
        Item item = repository.save(
                Item.builder()
                        .id(itemId)
                        .name(newItem.getName() == null ? oldItem.getName() : newItem.getName())
                        .description(newItem.getDescription() == null ?
                                oldItem.getDescription() : newItem.getDescription())
                        .owner(User.builder().id(userId).build())
                        .available(newItem.getAvailable() == null ?
                                oldItem.getAvailable() : newItem.getAvailable())
                        .request(newItem.getRequest() == null ?
                                oldItem.getRequest() : newItem.getRequest())
                        .build());

        return itemMapper.modelToDto(repository.save(item));
    }

    /**
     * Метод пролучения объекта из хранилища по id
     *
     * @param itemId id объекта класса Item
     * @throws NoFoundException если объект с переданным id отсутствует в хранилище
     */
    @Override
    public ItemWithBookingAndCommentDto getItemOfId(long userId, long itemId) {
        ItemWithBookingAndCommentDto item = itemMapper.modelToDtoWithBooking(repository.findById(itemId).orElseThrow(() -> {
            log.warn("Вещь с id: {} отсутствует", itemId);
            throw new NoFoundException("Вешь с id: " + itemId + " отсутствует");
        }));
        if (userId == item.getOwner().getId()) {
            item.setLastBooking(bookingService.getLastByItem(item.getId()));
            item.setNextBooking(bookingService.getNextByItem(item.getId()));
        }
        item.setComments(commentListMapper.modelsToInDtos(commentRepository
                .findCommentsByItemId(item.getId())));
        return item;

    }

    /**
     * Метод возвращает список объектов у которых поле Owner соответствет преданному параметру
     *
     * @param userId id объекта класса User
     */
    @Override
    public List<ItemWithBookingAndCommentDto> getItems(long userId, Pageable pageRequest) {
        User owner = userService.findUserByIdForValid(userId);
        List<ItemWithBookingAndCommentDto> items =
                itemListMapper.modelsToDtoWithBookings(repository.findItemsByOwnerEqualsOrderById(owner, pageRequest));
        List<ItemWithBookingAndCommentDto> itemsWithBooking = new ArrayList<>();
        for (ItemWithBookingAndCommentDto item : items) {
            item.setLastBooking(bookingService.getLastByItem(item.getId()));
            item.setNextBooking(bookingService.getNextByItem(item.getId()));
            item.setComments(commentListMapper.modelsToInDtos(commentRepository
                    .findCommentsByItemId(item.getId())));
            itemsWithBooking.add(item);
        }
        return itemsWithBooking;


    }

    /**
     * Метод возвращает список объектов из хранилища в поле name и description
     * которых встречается подстрока передаваемая в качестве параметра.
     * Если ничего не найдено, то возвращается пустой список
     */
    @Override
    public List<ItemDto> getItemOfText(long userId, String text, Pageable pageRequest) {
        if (text.isBlank()) return new ArrayList<>();
        User owner = userService.findUserByIdForValid(userId);
        return itemListMapper.modelsToDtos(repository
                .findItemsByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrueOrderById(
                        text, text, pageRequest));
    }

    /**
     * Метод добавления комментария к вещи
     *
     * @throws NoFoundException    если переданные userId и itemId невалидны
     * @throws ValidationException если автор комментания не бронировал вещь
     *                             которую комметрировал
     */
    @Override
    public CommentOutDto addComment(long userId, long itemId, CommentInDto commentInDto) {
        //проверяем валидность itemId и userId
        User user = userService.findUserByIdForValid(userId);
        Item item = repository.findById(itemId).orElseThrow(() -> {
            log.warn("Вещь с id: {} отсутствует", itemId);
            throw new NoFoundException("Вешь с id: " + itemId + " отсутствует");
        });
        if (bookingService.findAllFinishByItemByUser(userId, itemId).isEmpty()) {
            log.warn("Юзер c id {} не может добавить комментарий," +
                    " т.к. он не бронировал вещь c id {}", userId, itemId);
            throw new ValidationException("Юзер c id " + userId + " не может добавить" +
                    " комментарий, т.к. он не бронировал вещь c id " + itemId);
        }
        return commentMapper.modelToOutDto(commentRepository.save(Comment.builder()
                .text(commentInDto.getText())
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build()));
    }
}
