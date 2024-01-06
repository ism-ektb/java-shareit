package ru.practicum.shareit.booking.dtoMapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Mapper(componentModel = "spring", uses = BookingMapper.class)
public interface BookingListMapper {
    List<BookingDto> modelsToDtos(List<Booking> bookings);

    List<Booking> dtosToModels(List<BookingDto> bookingDtos);
}
