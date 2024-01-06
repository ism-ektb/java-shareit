package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.SimpleBookingDto;
import ru.practicum.shareit.exception.NoFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;
import java.util.Optional;

public interface BookingService {

    /**
     * метод для создания экземпляра бронирования. Ему присванивается новый Id ставится статус
     * WAITING
     *
     * @param simpleBookingDto упрощенное DTO модели Booking передает время начада и конца
     *                         бронирования и Id вещи (itemId), которая бронируется
     * @throws NoFoundException    если вещи с itemId не существует или юзер бронирует собственную вещь
     * @throws ValidationException если поле Available вещи == false
     */
    BookingDto create(Optional<Long> userId, SimpleBookingDto simpleBookingDto);

    /**
     * Метод для подтверждения или отклонения бронирования
     *
     * @throws NoFoundException    если подтверждает бронирование не собственник или если
     *                             бронирования с bookingId нет в базе
     * @throws ValidationException если собственник уже подтвердил бронирование
     */
    BookingDto approved(Optional<Long> userId, Long bookingId, Boolean approved);

    /**
     * Метод для получения информации о бронировании по его номеру
     *
     * @throws NoFoundException если такого бронирования не существет или
     *                          информацию о бронировании хочет получить не собственник
     *                          и не бронирующий юзер
     */
    BookingDto getById(Optional<Long> userId, Long bookingId);

    /**
     * Метод получения всех бронирований пользователя в зависимости от даты(времени)
     * и статуса бронирования, так же есть возможность получить список всех бронирований
     *
     * @throws NoFoundException если переданный userId не валиден
     */
    List<BookingDto> findAllForBooker(Optional<Long> userId, BookingStatus status);

    /**
     * Метод получения всех бронирований владельца вещей в зависимости от даты(времени)
     * и статуса бронирования, так же есть возможность получить список всех бронирований
     *
     * @throws NoFoundException если переданный userId не валиден
     */
    List<BookingDto> findAllForOwner(Optional<Long> ownerId, BookingStatus status);

    /**
     * Метод для получения ближайшего следующего бронирования вещи с переданным номером
     * входные данные не проверяются.
     */
    BookingForItemDto getLastByItem(Long itemId);

    /**
     * Метод для получения ближайшего следующего бронирования вещи с переданным номером
     * входные данные не проверяются.
     */
    BookingForItemDto getNextByItem(Long itemId);

    /**
     * Метод возвращает список заверщенных бронирований вещи с номером itemId
     * пользователем c номером userId
     * переданные в метод данные не проверяются.
     * в случае отсутствия бронирований возвращается пустой список
     */
    List<Booking> findAllFinishByItemByUser(Long userId, Long itemId);
}
