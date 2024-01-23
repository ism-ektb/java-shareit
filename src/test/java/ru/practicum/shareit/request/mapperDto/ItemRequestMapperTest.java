package ru.practicum.shareit.request.mapperDto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.ItemRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ItemRequestMapperTest {

    private final ItemRequestMapper mapper = new ItemRequestMapperImpl();

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
}