package ru.practicum.shareit.request.mapperDto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRequestListMapperTest {

    @Autowired
    private ItemRequestListMapper mapper;

    @Test
    void dtosToModels() {
        ItemRequest itemRequest = ItemRequest.builder().id(1L).build();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().id(1L).build();
        assertEquals(mapper.dtosToModels(List.of(itemRequestDto)), List.of(itemRequest));
    }
}