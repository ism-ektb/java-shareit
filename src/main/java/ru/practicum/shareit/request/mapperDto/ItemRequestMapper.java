package ru.practicum.shareit.request.mapperDto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {
    ItemRequest dtoToModel(ItemRequestDto itemRequestDto);

    ItemRequestDto modelToDto(ItemRequestDto itemRequestDto);
}
