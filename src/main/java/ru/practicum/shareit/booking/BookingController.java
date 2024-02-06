package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.SimpleBookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * Контроллер.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingService bookingService;

    /**
     * метод создания нового бронирования
     *
     * @param simpleBookingDto упрощенная сущьность бронирования
     * @throws MethodArgumentNotValidException при ошибке валидации simpleBookingDto
     */
    @PostMapping
    public BookingDto createBookingDto(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @Valid @RequestBody SimpleBookingDto simpleBookingDto) {
        BookingDto bookingDto = bookingService.create(userId, simpleBookingDto);
        return bookingDto;
    }

    /**
     * метод изменения статуса бронирования
     *
     * @param approved подтверждает или отклоняет бронирование
     * @throws MethodArgumentNotValidException при ошибке валидации simpleBookingDto
     */
    @PatchMapping("/{bookingId}")
    public BookingDto approved(@RequestHeader("X-Sharer-User-Id") long userId,
                               @PathVariable Long bookingId,
                               @RequestParam Boolean approved) {
        return bookingService.approved(userId, bookingId, approved);
    }

    /**
     * Метод получения бронирования по его номеру.
     * Доступно только букеру и хозяину вещи
     */
    @GetMapping("/{bookingId}")
    public BookingDto getBookingDtoById(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @PathVariable long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    /**
     * Метод получения бронирований букера с переданным в заголовке Id
     *
     * @throws ConversionFailedException если передан неверный параметр state
     */
    @GetMapping()
    public List<BookingDto>
    getBookingDtoByState(@RequestHeader("X-Sharer-User-Id") long userId,
                         @RequestParam(required = false,
                                 defaultValue = "ALL") BookingStatus state,
                         @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                         @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        return bookingService.findAllForBooker(userId, state,  PageRequest.of(from / size, size));
    }

    /**
     * Метод получения бронирований владельца вещей с переданным в заголовке Id
     *
     * @throws ConversionFailedException если передан неверный параметр state
     */
    @GetMapping("/owner")
    public List<BookingDto>
    getBookingDtoByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                         @RequestParam(required = false, defaultValue = "ALL")
                         BookingStatus state,
                         @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                         @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        return bookingService.findAllForOwner(userId, state,  PageRequest.of(from / size, size));
    }
}
