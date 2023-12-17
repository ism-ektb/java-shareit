package ru.practicum.shareit.item.dtoMapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.mapperDto.ItemRequestMapper;
import ru.practicum.shareit.user.mapperDto.UserMapper;

@Mapper(componentModel = "spring", uses = {ItemRequestMapper.class, UserMapper.class})
public interface ItemMapper {
    Item dtoToModel(ItemDto itemDto);

    ItemDto modelToDto(Item item);
}
