package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.mapperDto.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.booking.BookingStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
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
    public BookingDto create(long userId, SimpleBookingDto simpleBookingDto) {
        //неявно проверяем валидность itemId и userId
        Item item = itemRepository.findById(simpleBookingDto.getItemId()).orElseThrow(() -> {
            log.warn("Вещь с id: {} отсутствует", simpleBookingDto.getItemId());
            throw new NoFoundException("Вешь с id: " + simpleBookingDto.getItemId() + " отсутствует");
        });
        User user = userService.findUserByIdForValid(userId);
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
    public BookingDto approved(long userId, Long bookingId, Boolean approved) {
        //неявно проверяем валидность bookingId
        Booking booking = repository.findById(bookingId).orElseThrow(() -> {
                log.warn("бронирования с id {} нет в базе", bookingId);
        throw new NoFoundException("бронирования с id " +
                bookingId + " нет в базе"); });
        User user = userService.findUserByIdForValid(userId);
        if (!(booking.getItem().getOwner().equals(user))) {
            log.warn("Подтвердить бронь с id {} может только хозяин вещи", bookingId);
            throw new NoFoundException("Подтвердить бронь c Id " +
                    bookingId + " может только хозяин вещи");
        }
        if (!(booking.getStatus().equals(WAITING))) {
            log.warn("бронирование: {} уже подтверрждено", booking.toString());
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
    public BookingDto getById(long userId, long bookingId) {
        Booking booking = repository.findById(bookingId).orElseThrow(() -> {
            log.warn("бронирования с id {} нет в базе", bookingId);
            throw new NoFoundException("бронирования с id " +
                    bookingId + " нет в базе");
        });
        User user = userService.findUserByIdForValid(userId);
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
    public List<BookingDto>
    findAllForBooker(long userId, BookingStatus state, Pageable pageRequest) {
        //неявно проверяем валидность userId
        userService.findUserByIdForValid(userId);
        List<Booking> findBooking;
        switch (state) {
            case ALL:
                findBooking = repository.findAllByBookerIdOrderByStartDesc(userId, pageRequest);
                break;
            case WAITING:
            case REJECTED:
            case CANCELED:
            case APPROVED:
                findBooking = repository.findAllByBookerIdAndAndStatusEqualsOrderByStartDesc(
                        userId, state, pageRequest);
                break;
            case FUTURE:
                findBooking = repository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                        userId, LocalDateTime.now(), pageRequest);
                break;
            case PAST:
                findBooking = repository.findAllByBookerIdAndAndEndBeforeOrderByStartDesc(
                        userId, LocalDateTime.now(), pageRequest);
                break;
            case CURRENT:
                findBooking = repository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        userId, LocalDateTime.now(), LocalDateTime.now(), pageRequest);
                break;
            default:
                findBooking = null;
        }
        return bookingListMapper.modelsToDtos(findBooking);
    }


    /**
     * Метод получения всех бронирований владельца вещей в зависимости от даты(времени)
     * и статуса бронирования, так же есть возможность получить список всех бронирований
     *
     * @throws NoFoundException если переданный userId не валиден
     */
    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findAllForOwner(long ownerId, BookingStatus state, Pageable pageRequest) {
        //неявно проверяем валидность userId
        userService.findUserByIdForValid(ownerId);
        List<Booking> findBooking;

        switch (state) {
            case ALL:
                findBooking = repository.findAllByItemOwnerIdOrderByStartDesc(ownerId, pageRequest);
                break;
            case WAITING:
            case REJECTED:
            case CANCELED:
            case APPROVED:
                findBooking = repository.findAllByItemOwnerIdAndAndStatusEqualsOrderByStartDesc(
                        ownerId, state, pageRequest);
                break;
            case FUTURE:
                findBooking = repository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
                        ownerId, LocalDateTime.now(), pageRequest);
                break;
            case PAST:
                findBooking = repository.findAllByItemOwnerIdAndAndEndBeforeOrderByStartDesc(
                        ownerId, LocalDateTime.now(), pageRequest);
                break;
            case CURRENT:
                findBooking = repository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        ownerId, LocalDateTime.now(), LocalDateTime.now(), pageRequest);
                break;
            default:
                findBooking = null;
        }
        return bookingListMapper.modelsToDtos(findBooking);
    }

    /**
     * Метод для получения последнего бронирования вещи с переданным номером
     * входные данные не проверяются.
     */
    @Override
    @Transactional(readOnly = true)
    public BookingForItemDto getLastByItem(Long itemId) {
        List<Booking> findBookings = repository.findAllLast(
                itemId, LocalDateTime.now(), APPROVED, WAITING);
        return bookingMapper.modelToDtoForItem(
                findBookings.isEmpty() ? null : findBookings.get(0));
    }

    /**
     * Метод для получения ближайшего следующего бронирования вещи с переданным номером
     * входные данные не проверяются.
     */
    @Override
    @Transactional(readOnly = true)
    public BookingForItemDto getNextByItem(Long itemId) {
        List<Booking> findBookings = repository.findAllNext(
                itemId, LocalDateTime.now(), APPROVED, WAITING);
        return bookingMapper.modelToDtoForItem(
                findBookings.isEmpty() ? null : findBookings.get(0));
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
        return repository.findAllFinishByBookerIdByItemId(
                userId, itemId, LocalDateTime.now());
    }
}
