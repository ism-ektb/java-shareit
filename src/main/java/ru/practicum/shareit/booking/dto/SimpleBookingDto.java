package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.ToString;
import ru.practicum.shareit.annotation.ValidStartIsBeforeEnd;

import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * упрощенное DTO модели Booking для получения данных от пользователя
 */
@Getter
@ToString
@ValidStartIsBeforeEnd
public class SimpleBookingDto {
    @NotNull
    @Min(1)
    private Long itemId;
    @NotNull
    @Future
    private LocalDateTime start;
    @NotNull
    @Future
    private LocalDateTime end;
}
