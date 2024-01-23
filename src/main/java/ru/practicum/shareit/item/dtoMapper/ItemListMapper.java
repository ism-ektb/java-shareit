package ru.practicum.shareit.item.dtoMapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.mapperDto.ItemRequestMapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ItemRequestMapper.class, ItemMapper.class})
public interface ItemListMapper {
    List<Item> dtosToModels(List<ItemDto> dtos);

    List<ItemDto> modelsToDtos(List<Item> items);

    List<ItemWithBookingAndCommentDto> modelsToDtoWithBookings(List<Item> items);

    List<ItemForRequestDto> modelsToDtosForRequest(List<Item> items);
}
