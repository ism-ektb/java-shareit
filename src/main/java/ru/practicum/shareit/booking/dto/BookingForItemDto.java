package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * упрощенное DTO модели Booker для формирования DTO модели Item
 */
@Getter
@Builder
public class BookingForItemDto {
    private Long id;
    private Long bookerId;
}
