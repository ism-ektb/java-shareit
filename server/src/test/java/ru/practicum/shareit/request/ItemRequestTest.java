package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestTest {

    @Test
    void setId() {
        ItemRequest itemRequest = ItemRequest.builder().build();
        ItemRequest itemRequest1 = ItemRequest.builder().id(1L).build();
        itemRequest.setId(1L);
        assertEquals(itemRequest, itemRequest1);
    }

    @Test
    void testToString() {
        ItemRequest itemRequest = ItemRequest.builder().build();
        assertEquals(itemRequest.toString(), "ItemRequest(id=0, description=null, requestor=null, created=null)");
    }
}