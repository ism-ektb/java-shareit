package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserWithoutEmailDto;

import java.time.LocalDateTime;

/**
 * DTO модели booking для возврата после успешного создания бронирования.
 */
@Getter
@Builder
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDto item;
    private UserWithoutEmailDto booker;
    private BookingStatus status;
}
