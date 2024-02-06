package ru.practicum.shareit.item.dtoMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserWithoutEmailDto;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ItemMapperTest {

    @Autowired
    private ItemMapper mapper;

    @Test
    void dtoToModel() {
        ItemDto itemDto = ItemDto.builder()
                .name("name")
                .owner(UserWithoutEmailDto.builder().id(1L).build()).requestId(1L).build();
        Item item = Item.builder()
                .name("name")
                .owner(User.builder().id(1L).build())
                .request(ItemRequest.builder().id(1L).build())
                .build();
        assertEquals(item, mapper.dtoToModel(itemDto));
    }

    @Test
    void dtoToModelWhenInputParamRequestIdAbsent() {
        ItemDto itemDto = ItemDto.builder().name("name").build();
        Item item = Item.builder()
                .name("name")
                .build();
        assertEquals(item, mapper.dtoToModel(itemDto));
    }

    @Test
    void modelToDto_usersEmailDontSend() {
        ItemDto itemDto = ItemDto.builder()
                .name("name")
                .owner(UserWithoutEmailDto.builder()
                        .id(1L)
                        .name("user_name")
                        .build())
                .requestId(1L).build();
        Item item = Item.builder()
                .name("name")
                .request(ItemRequest.builder().id(1L).build())
                .owner(User.builder()
                        .id(1L)
                        .name("user_name")
                        .email("e@dfg.ee")
                        .build())
                .build();
        assertEquals(itemDto, mapper.modelToDto(item));
    }

    @Test
    void modelToDto_requestIsAbsent() {
        ItemDto itemDto = ItemDto.builder()
                .name("name")
                .build();
        Item item = Item.builder()
                .name("name")
                .build();
        assertEquals(itemDto, mapper.modelToDto(item));
    }

    @Test
    void modelToDtoForRequest() {
        ItemForRequestDto itemForRequestDto = ItemForRequestDto.builder()
                .name("name")
                .requestId(1L).build();
        Item item = Item.builder()
                .name("name")
                .request(ItemRequest.builder().id(1L).build())
                .owner(User.builder()
                        .id(1L)
                        .name("user_name")
                        .email("e@dfg.ee")
                        .build())
                .build();
        assertEquals(itemForRequestDto, mapper.modelToDtoForRequest(item));
    }

    @Test
    void modelToDtoForRequest_RequestIsAbsent() {
        ItemForRequestDto itemForRequestDto = ItemForRequestDto.builder()
                .name("name").build();
        Item item = Item.builder()
                .name("name")
                .owner(User.builder()
                        .id(1L)
                        .name("user_name")
                        .email("e@dfg.ee")
                        .build())
                .build();
        assertEquals(itemForRequestDto, mapper.modelToDtoForRequest(item));
    }

    @Test
    void modelToDtoWithBooking() {
        Item item = Item.builder().id(1L).build();
        ItemWithBookingAndCommentDto itemDto = ItemWithBookingAndCommentDto.builder().id(1L).build();
        assertEquals(mapper.modelToDtoWithBooking(item), itemDto);
    }
}