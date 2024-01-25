package ru.practicum.shareit.item.dtoMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemListMapperTest {

    @Autowired
    private ItemListMapper mapper;

    @Test
    void dtosToModels() {
        Item item = Item.builder().id(1L).build();
        ItemDto itemDto = ItemDto.builder().id(1L).build();
        assertEquals(mapper.dtosToModels(List.of(itemDto)), List.of(item));
    }

    @Test
    void modelsToDtos() {
        Item item = Item.builder().id(1L).build();
        ItemDto itemDto = ItemDto.builder().id(1L).build();
        assertEquals(mapper.modelsToDtos(List.of(item)), List.of(itemDto));
    }

    @Test
    void modelsToDtoWithBookings() {
        Item item = Item.builder().id(1L).build();
        ItemWithBookingAndCommentDto itemDto = ItemWithBookingAndCommentDto.builder().id(1L).build();
        assertEquals(mapper.modelsToDtoWithBookings(List.of(item)), List.of(itemDto));
    }

    @Test
    void modelsToDtosForRequest() {
        Item item = Item.builder().id(1L).build();
        ItemForRequestDto itemDto = ItemForRequestDto.builder().id(1L).build();
        assertEquals(mapper.modelsToDtosForRequest(List.of(item)), List.of(itemDto));
    }
}