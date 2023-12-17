package ru.practicum.shareit.request.mapperDto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Mapper(componentModel = "spring", uses = ItemRequestMapper.class)
public interface ItemRequestListMapper {
    List<ItemRequest> dtosToModels(List<ItemRequestDto> dtos);
  //  List<ItemRequestDto> modelsToDtos(List<ItemRequest> itemRequests);
}
