package ru.practicum.shareit.item.dtoMapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dtoMapper.BookingMapper;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.mapperDto.ItemRequestMapper;
import ru.practicum.shareit.user.mapperDto.UserMapper;

@Mapper(componentModel = "spring", uses = {ItemRequestMapper.class,
        UserMapper.class, BookingMapper.class, CommentMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ItemMapper {

    @Mapping(target = "request", source = "itemDto.requestId")
    Item dtoToModel(ItemDto itemDto);

    @Mapping(target = "requestId", source = "item.request.id")
    ItemDto modelToDto(Item item);

    ItemWithBookingAndCommentDto
    modelToDtoWithBooking(Item item);

    @Mapping(target = "requestId", source = "item.request.id")
    ItemForRequestDto modelToDtoForRequest(Item item);


}
