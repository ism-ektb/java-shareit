package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.SimpleBookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

/**
 * Контроллер.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient client;

    /**
     * метод создания нового бронирования
     * Возвращает BookingDto
     *
     * @param simpleBookingDto упрощенная сущьность бронирования
     * @throws MethodArgumentNotValidException при ошибке валидации simpleBookingDto
     */
    @PostMapping
    public ResponseEntity<Object> createBookingDto(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @Valid @RequestBody SimpleBookingDto simpleBookingDto) {
        return client.create(userId, simpleBookingDto);
    }

    /**
     * метод изменения статуса бронирования
     * Возвращает BookingDto
     *
     * @param approved подтверждает или отклоняет бронирование
     * @throws MethodArgumentNotValidException при ошибке валидации simpleBookingDto
     */
    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approved(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PathVariable long bookingId,
                                           @RequestParam Boolean approved) {
        return client.approved(userId, bookingId, approved);
    }

    /**
     * Метод получения бронирования по его номеру.
     * Возвращает BookingDto
     * Доступно только букеру и хозяину вещи
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingDtoById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @PathVariable long bookingId) {
        return client.getById(userId, bookingId);
    }

    /**
     * Метод получения бронирований букера с переданным в заголовке Id
     * Возвращает List<BookingDto>
     *
     * @throws ConversionFailedException если передан неверный параметр state
     */
    @GetMapping()
    public ResponseEntity<Object>
    getBookingDtoByState(@RequestHeader("X-Sharer-User-Id") long userId,
                         @RequestParam(required = false,
                                 defaultValue = "ALL") BookingState state,
                         @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                         @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        return client.findAllForBooker(userId, state, from, size);
    }

    /**
     * Метод получения бронирований владельца вещей с переданным в заголовке Id
     * Возвращает List<BookingDto>
     *
     * @throws ConversionFailedException если передан неверный параметр state
     */
    @GetMapping("/owner")
    public ResponseEntity<Object>
    getBookingDtoByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                         @RequestParam(required = false, defaultValue = "ALL")
                         BookingState state,
                         @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                         @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        return client.findAllForOwner(userId, state, from, size);
    }
}
