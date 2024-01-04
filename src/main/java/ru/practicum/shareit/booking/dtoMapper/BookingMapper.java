package ru.practicum.shareit.booking.dtoMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.item.dtoMapper.ItemMapper;
import ru.practicum.shareit.user.mapperDto.UserMapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class})
public interface BookingMapper {
    BookingDto modelToDto(Booking booking);
    Booking dtoToModel(BookingDto bookingDto);
    @Mapping(target = "bookerId", source = "booking.booker.id")
    BookingForItemDto modelToDtoForItem(Booking booking);
}
