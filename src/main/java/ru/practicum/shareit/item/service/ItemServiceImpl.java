package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentInDto;
import ru.practicum.shareit.comment.dto.CommentOutDto;
import ru.practicum.shareit.comment.mapper.CommentListMapper;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.exception.FormatDataException;
import ru.practicum.shareit.exception.NoFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemStorage;
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
import java.util.Optional;

/**
 * Сервисный класс для работы с объектами класса Item
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final ItemMapper itemMapper;
    private final ItemListMapper itemListMapper;
    private final UserValidator itemValidator;
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
     * @throws FormatDataException если переданные в метод данные не соответствуют
     *                             "бизнес-логике"
     * @throws NoFoundException    если объект с userId не существует
     */
    @Override
    public ItemDto createItem(ItemDto itemDto, Optional<Long> userId) {
        itemValidator.checkCreateAndPatch(userId);
        Item item = itemMapper.dtoToModel(itemDto);
        User user = userMapper.dtoToModel(userService.findUserById(userId.get()));
        //полностью заполняем поле owner
        item.setOwner(user);
        return itemMapper.modelToDto(repository.save(item));
    }

    /**
     * Метод для обновления данных об объекте в базе данных
     *
     * @param itemDto данные для обновления
     * @param itemId id обновляемого объекта
     * @param userId  id зарегистрированного пользователя обновляющего объект
     * @throws FormatDataException если переданные в метод данные не соответствуют
     *                             "бизнес-логике"
     * @throws NoFoundException если объект с userId не существует
     */
    @Override
    public ItemDto updateItem(Optional<Long> userId, long itemId, ItemDto itemDto) {
        itemValidator.checkCreateAndPatch(userId);
        Item newItem = itemMapper.dtoToModel(itemDto);
        //неявно проверяем что userId валидно
        User user = userMapper.dtoToModel(userService.findUserById(userId.get()));
        //полностью заполняем поле owner
        Item oldItem = repository.findById(itemId).orElseThrow(() -> {
            log.warn("Вещь с id: {} отсутствует", itemId);
            throw new NoFoundException("Вешь с id: " + itemId + " отсутствует");
        });

        if (newItem.getOwner() != null && (!(newItem.getOwner().equals(user)))) {
            log.warn("Для смены хозяина вещи {} используйте другой метод",
                    repository.findById(itemId));
            throw new NoFoundException("Для смены хозяина вещи "
                    + repository.findById(itemId) + " используйте другой метод");
        }

        Item item = repository.save(
                Item.builder()
                        .id(itemId)
                        .name(newItem.getName() == null ? oldItem.getName() : newItem.getName())
                        .description(newItem.getDescription() == null ?
                                oldItem.getDescription() : newItem.getDescription())
                        .owner(User.builder().id(userId.get()).build())
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
     * @throws NoFoundException    если объект с переданным id отсутствует в хранилище
     * @throws FormatDataException если пользователь запрашивающий данные не зарегистрирован
     */
    @Override
    public ItemWithBookingAndCommentDto getItemOfId(Optional<Long> userId, long itemId) {
        itemValidator.checkGetRequest(userId);
        ItemWithBookingAndCommentDto item = itemMapper.modelToDtoWithBooking(repository.findById(itemId).orElseThrow(() -> {
            log.warn("Вещь с id: {} отсутствует", itemId);
            throw new NoFoundException("Вешь с id: " + itemId + " отсутствует");
        }));
        if (userId.get() == item.getOwner().getId()) {
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
     * @throws FormatDataException если пользователь запрашивающий данные не зарегистрирован
     */
    @Override
    public List<ItemWithBookingAndCommentDto> getItems(Optional<Long> userId) {
        itemValidator.checkGetRequest(userId);
        User owner = userMapper.dtoToModel(userService.findUserById(userId.get()));
        List<ItemWithBookingAndCommentDto> items =
                itemListMapper.modelsToDtoWithBookings(repository.findItemsByOwnerEqualsOrderById(owner));
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
     *
     * @throws FormatDataException если пользователь запрашивающий данные не зарегистрирован
     */
    @Override
    public List<ItemDto> getItemOfText(Optional<Long> userId, String text) {
        itemValidator.checkGetRequest(userId);
        if (text.isBlank()) return new ArrayList<>();
        return itemListMapper.modelsToDtos(new ArrayList<>(repository
                .findItemsByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrueOrderById(
                        text, text)));
    }

    /**
     * Метод добавления комментария к вещи
     *
     * @throws NoFoundException    если переданные userId и itemId невалидны
     * @throws ValidationException если автор комментания не бронировал вещь
     *                             которую комметрировал
     */
    @Override
    public CommentOutDto addComment(Optional<Long> userId, Long itemId, CommentInDto commentInDto) {
        itemValidator.checkCreateAndPatch(userId);
        //проверяем валидность itemId и userId
        User user = userMapper.dtoToModel(userService.findUserById(userId.get()));
        Item item = repository.findById(itemId).orElseThrow(() -> {
            log.warn("Вещь с id: {} отсутствует", itemId);
            throw new NoFoundException("Вешь с id: " + itemId + " отсутствует");
        });
        if (bookingService.findAllFinishByItemByUser(userId.get(), itemId).isEmpty()) {
            log.warn("Юзер c id {} не может добавить комментарий," +
                    " т.к. он не бронировал вещь c id {}", userId.get(), itemId);
            throw new ValidationException("Юзер c id " + userId.get() + " не может добавить" +
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
