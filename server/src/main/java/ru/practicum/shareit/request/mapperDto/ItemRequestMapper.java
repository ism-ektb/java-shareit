package ru.practicum.shareit.request.mapperDto;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsForThisReqDto;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ItemRequestMapper {
    ItemRequest dtoToModel(ItemRequestDto itemRequestDto);

    ItemRequestDto modelToDto(ItemRequest itemRequest);

    ItemRequestWithItemsForThisReqDto modelToDtoWithListOfItem(ItemRequest itemRequest);

    @Mapping(target = "id", source = "requestId")
    ItemRequest longToModel(Long requestId);
}
