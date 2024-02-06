package ru.practicum.shareit.request.mapperDto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class ItemRequestMapperTest {

    @Autowired
    private  ItemRequestMapper mapper;

    @Test
    void longToModel() {

        long l = 1L;
        ItemRequest itemRequest = ItemRequest.builder().id(l).build();
        assertEquals(itemRequest, mapper.longToModel(l));
    }

    @Test
    void longToModelWhenInputParamEqualsNull() {

        Long l = null;
        assertNull(mapper.longToModel(l));
    }

    @Test
    void dtoToModel() {
        ItemRequest itemRequest = ItemRequest.builder().id(1L).build();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().id(1L).build();
        assertEquals(mapper.dtoToModel(itemRequestDto), itemRequest);
    }

    @Test
    void modelToDto() {
        ItemRequest itemRequest = ItemRequest.builder().id(1L).build();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().id(1L).build();
        assertEquals(mapper.modelToDto(itemRequest), itemRequestDto);
    }
}