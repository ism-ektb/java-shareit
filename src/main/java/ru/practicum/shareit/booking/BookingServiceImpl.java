package ru.practicum.shareit.booking;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.SimpleBookingDto;
import ru.practicum.shareit.booking.dtoMapper.BookingListMapper;
import ru.practicum.shareit.booking.dtoMapper.BookingMapper;
import ru.practicum.shareit.exception.NoFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.UserValidator;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.mapperDto.UserMapper;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static ru.practicum.shareit.booking.BookingStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
    private final UserValidator userValidator;
    private final BookingMapper bookingMapper;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final UserMapper userMapper;
    private final BookingListMapper bookingListMapper;

    /**
     * метод для создания экземпляра бронирования. Ему присванивается новый Id ставится статус
     * WAITING
     *
     * @param simpleBookingDto упрощенное DTO модели Booking передает время начада и конца
     *                         бронирования и Id вещи (itemId), которая бронируется
     * @throws NoFoundException    если вещи с itemId не существует или юзер бронирует собственную вещь
     * @throws ValidationException если поле Available вещи == false
     */
    @Override
    @Transactional
    public BookingDto create(Optional<Long> userId, SimpleBookingDto simpleBookingDto) {
        userValidator.checkCreateAndPatch(userId);
        //неявно проверяем валидность itemId и userId
        Item item = itemRepository.findById(simpleBookingDto.getItemId()).orElseThrow(() -> {
            log.warn("Вещь с id: {} отсутствует", simpleBookingDto.getItemId());
            throw new NoFoundException("Вешь с id: " + simpleBookingDto.getItemId() + " отсутствует");
        });
        User user = userMapper.dtoToModel(userService.findUserById(userId.get()));
        if (!(item.getAvailable())) {
            log.warn("Бронирование {}  вещи {} невозможно", simpleBookingDto.toString(),
                    item.toString());
            throw new ValidationException("бронирование вещи " + item.toString() + " не возможно");
        }
        if (item.getOwner().equals(user)) {
            log.warn("бронирование собственной вещи {} не возможно", item.toString());
            throw new NoFoundException("бронирование собственной вещи " + item.toString() +
                    " невозможно");
        }
        return bookingMapper.modelToDto(repository.save(Booking.builder()
                .start(simpleBookingDto.getStart())
                .end(simpleBookingDto.getEnd())
                .item(item)
                .booker(user)
                .status(WAITING)
                .build()));
    }

    /**
     * Метод для подтверждения или отклонения бронирования
     *
     * @throws NoFoundException    если подтверждает бронирование не собственник или если
     *                             бронирования с bookingId нет в базе
     * @throws ValidationException если собственник уже подтвердил бронирование
     */
    @Override
    @Transactional
    public BookingDto approved(Optional<Long> userId, Long bookingId, Boolean approved) {
        userValidator.checkCreateAndPatch(userId);
        //неявно проверяем валидность bookingId
        Booking booking = bookingMapper.dtoToModel(getById(userId, bookingId));
        User user = userMapper.dtoToModel(userService.findUserById(userId.get()));
        if (!(booking.getItem().getOwner().equals(user))) {
            log.warn("Подтвердить бронь с id {} может только хозяин вещи", bookingId);
            throw new NoFoundException("Подтвердить бронь c Id " +
                    bookingId + " может только хозяин вещи");
        }
        if (!(booking.getStatus().equals(WAITING))) {
            log.warn("бронирование {} уже подтверрждено", booking.toString());
            throw new ValidationException("бронирование " + booking.toString() + " уже подтверждено");
        }
        booking.setStatus(approved ? APPROVED : REJECTED);
        return bookingMapper.modelToDto(repository.save(booking));
    }

    /**
     * Метод для получения информации о бронировании по его номеру
     *
     * @throws NoFoundException если такого бронирования не существет или
     *                          информацию о бронировании хочет получить не собственник
     *                          и не бронирующий юзер
     */
    @Override
    @Transactional(readOnly = true)
    public BookingDto getById(Optional<Long> userId, Long bookingId) {
        userValidator.checkCreateAndPatch(userId);
        Booking booking = repository.findById(bookingId).orElseThrow(() -> {
            log.warn("бронирования с id {} нет в базе", bookingId);
            throw new NoFoundException("бронирования с id " +
                    bookingId + " нет в базе");
        });
        User user = userMapper.dtoToModel(userService.findUserById(userId.get()));
        if ((!(booking.getBooker().equals(user)))
                && !(booking.getItem().getOwner().equals(user))) {
            log.info("информация о бронировании доступна только владельцу вещи и автору брони");
            throw new NoFoundException("информация о бронировании доступна" +
                    " только владельцу вещи и автору брони");
        }
        return bookingMapper.modelToDto(booking);
    }

    /**
     * Метод получения всех бронирований пользователя в зависимости от даты(времени)
     * и статуса бронирования, так же есть возможность получить список всех бронирований
     *
     * @throws NoFoundException если переданный userId не валиден
     */
    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findAllForBooker(Optional<Long> userId, BookingStatus state) {

        userValidator.checkCreateAndPatch(userId);
        //неявно проверяем валидность userId
        User user = userMapper.dtoToModel(userService.findUserById(userId.get()));
        Iterable findBooking;

        BooleanExpression byUserId = QBooking.booking.booker.id.eq(userId.get());
        BooleanExpression byStatus = QBooking.booking.status.eq(state);
        BooleanExpression byStartAfterNow = QBooking.booking.start.after(LocalDateTime.now());
        BooleanExpression byEndBeforeNow = QBooking.booking.end.before(LocalDateTime.now());
        BooleanExpression byStartBeforeNow = QBooking.booking.start.before(LocalDateTime.now());
        BooleanExpression byEndAfterNow = QBooking.booking.end.after(LocalDateTime.now());

        switch (state) {
            case ALL:
                findBooking = repository.findAll(byUserId);
                break;
            case WAITING:
            case REJECTED:
            case CANCELED:
            case APPROVED:
                findBooking = repository.findAll(byUserId.and(byStatus));
                break;
            case FUTURE:
                findBooking = repository.findAll(byUserId.and(byStartAfterNow));
                break;
            case PAST:
                findBooking = repository.findAll(byUserId.and(byEndBeforeNow));
                break;
            case CURRENT:
                findBooking = repository.findAll(byUserId.and(byStartBeforeNow).and(byEndAfterNow));
                break;
            default:
                findBooking = repository.findAll(byUserId.and(byStartBeforeNow));
        }
        @SuppressWarnings("unchecked")
        Iterable<Booking> iterable = findBooking;
        List<Booking> bookings = StreamSupport.stream(iterable.spliterator(), false)
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .collect(Collectors.toList());
        return bookingListMapper.modelsToDtos(bookings);
    }


    /**
     * Метод получения всех бронирований владельца вещей в зависимости от даты(времени)
     * и статуса бронирования, так же есть возможность получить список всех бронирований
     *
     * @throws NoFoundException если переданный userId не валиден
     */
    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findAllForOwner(Optional<Long> ownerId, BookingStatus state) {

        userValidator.checkCreateAndPatch(ownerId);
        //неявно проверяем валидность userId
        User user = userMapper.dtoToModel(userService.findUserById(ownerId.get()));
        Iterable findBooking;

        BooleanExpression byOwnerId = QBooking.booking.item.owner.id.eq(ownerId.get());
        BooleanExpression byStatus = QBooking.booking.status.eq(state);
        BooleanExpression byStartAfterNow = QBooking.booking.start.after(LocalDateTime.now());
        BooleanExpression byEndBeforeNow = QBooking.booking.end.before(LocalDateTime.now());
        BooleanExpression byStartBeforeNow = QBooking.booking.start.before(LocalDateTime.now());
        BooleanExpression byEndAfterNow = QBooking.booking.end.after(LocalDateTime.now());

        switch (state) {
            case ALL:
                findBooking = repository.findAll(byOwnerId);
                break;
            case WAITING:
            case REJECTED:
            case CANCELED:
            case APPROVED:
                findBooking = repository.findAll(byOwnerId.and(byStatus));
                break;
            case FUTURE:
                findBooking = repository.findAll(byOwnerId.and(byStartAfterNow));
                break;
            case PAST:
                findBooking = repository.findAll(byOwnerId.and(byEndBeforeNow));
                break;
            case CURRENT:
                findBooking = repository.findAll(byOwnerId.and(byStartBeforeNow).and(byEndAfterNow));
                break;
            default:
                findBooking = repository.findAll(byOwnerId.and(byStartBeforeNow));
        }
        @SuppressWarnings("unchecked")
        Iterable<Booking> iterable = findBooking;
        List<Booking> bookings = StreamSupport.stream(iterable.spliterator(), false)
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .collect(Collectors.toList());
        return bookingListMapper.modelsToDtos(bookings);
    }

    /**
     * Метод для получения последнего бронирования вещи с переданным номером
     * входные данные не проверяются.
     */
    @Override
    @Transactional(readOnly = true)
    public BookingForItemDto getLastByItem(Long itemId) {

        BooleanExpression byItemId = QBooking.booking.item.id.eq(itemId);
        BooleanExpression byStartBeforeNow = QBooking.booking.start.before(LocalDateTime.now());
        BooleanExpression byStatus = QBooking.booking.status.in(List.of(APPROVED, WAITING));
        @SuppressWarnings("unchecked")
        Iterable<Booking> findBooking = repository
                .findAll(byItemId.and(byStartBeforeNow).and(byStatus));
        Booking booking = StreamSupport.stream(findBooking.spliterator(), false)
                .max(Comparator.comparing(Booking::getEnd))
                .orElse(null);
        return bookingMapper.modelToDtoForItem(booking);
    }

    /**
     * Метод для получения ближайшего следующего бронирования вещи с переданным номером
     * входные данные не проверяются.
     */
    @Override
    @Transactional(readOnly = true)
    public BookingForItemDto getNextByItem(Long itemId) {
        BooleanExpression byItemId = QBooking.booking.item.id.eq(itemId);
        BooleanExpression byStartAfter = QBooking.booking.start.after(LocalDateTime.now());
        BooleanExpression byStatus = QBooking.booking.status.in(List.of(APPROVED, WAITING));
        @SuppressWarnings("unchecked")
        Iterable<Booking> findBooking = repository.findAll(byItemId
                .and(byStartAfter).and(byStatus));
        Booking booking = StreamSupport.stream(findBooking.spliterator(), false)
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);
        return bookingMapper.modelToDtoForItem(booking);
    }

    /**
     * Метод возвращает список заверщенных бронирований вещи с номером itemId
     * пользователем c номером userId
     * переданные в метод данные не проверяются.
     * в случае отсутствия бронирований возвращается пустой список
     */
    @Override
    @Transactional(readOnly = true)
    public List<Booking> findAllFinishByItemByUser(Long userId, Long itemId) {
        BooleanExpression byItemId = QBooking.booking.item.id.eq(itemId);
        BooleanExpression byBookerId = QBooking.booking.booker.id.eq(userId);
        BooleanExpression byFinish = QBooking.booking.end.before(LocalDateTime.now());
        BooleanExpression byStatus = QBooking.booking.status.eq(APPROVED);
        @SuppressWarnings("unchecked")
        Iterable<Booking> findBooking = repository.findAll(byBookerId.and(byItemId)
                .and(byFinish).and(byStatus));
        return StreamSupport.stream(findBooking.spliterator(), false)
                .collect(Collectors.toList());
    }
}
