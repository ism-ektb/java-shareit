package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.dtoMapper.ItemListMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsForThisReqDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class ItemRequestServiceImplTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @MockBean
    private ItemRequestRepository repository;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private UserService userService;

    @Autowired
    private ItemListMapper itemListMapper;

    @Test
    void getReqUserWithItemsForThisReq() {
        Item item1 = new Item();
        Item item2 = new Item();
        List<Item> items = List.of(item1, item2);
        ItemRequest itemRequest1 = ItemRequest.builder().id(1L).build();
        ItemRequest itemRequest2 = ItemRequest.builder().id(2L).build();
        when(userService.findUserByIdForValid(anyLong())).thenReturn(new User());
        when(repository.findAllByRequestorIdOrderByCreatedDesc(anyLong(), any()))
                .thenReturn(List.of(itemRequest1, itemRequest2));
        when(itemRepository.findAllByRequestId(anyLong()))
                .thenReturn(items);

        List<ItemForRequestDto> dtoItemList = itemListMapper.modelsToDtosForRequest(items);
        ItemRequestWithItemsForThisReqDto itemRequestWithItemsForThisReqDto1 =
                ItemRequestWithItemsForThisReqDto.builder()
                        .id(1L)
                        .items(dtoItemList).build();
        ItemRequestWithItemsForThisReqDto itemRequestWithItemsForThisReqDto2 =
                ItemRequestWithItemsForThisReqDto.builder()
                        .id(2L)
                        .items(dtoItemList).build();
        List<ItemRequestWithItemsForThisReqDto> list = List.of(itemRequestWithItemsForThisReqDto1,
                itemRequestWithItemsForThisReqDto2);

        assertEquals(list, itemRequestService.getReqUserWithItemsForThisReq(1L, PageRequest.of(1, 4)));
    }

    @Test
    void getReqAllWithItemsForThisReq() {
        Item item1 = new Item();
        Item item2 = new Item();
        List<Item> items = List.of(item1, item2);
        ItemRequest itemRequest1 = ItemRequest.builder().id(1L).build();
        ItemRequest itemRequest2 = ItemRequest.builder().id(2L).build();
        when(userService.findUserByIdForValid(anyLong())).thenReturn(new User());
        when(repository.findAllByRequestorIdIsNotOrderByCreatedDesc(anyLong(), any()))
                .thenReturn(List.of(itemRequest1, itemRequest2));
        when(itemRepository.findAllByRequestId(anyLong()))
                .thenReturn(items);

        List<ItemForRequestDto> dtoItemList = itemListMapper.modelsToDtosForRequest(items);
        ItemRequestWithItemsForThisReqDto itemRequestWithItemsForThisReqDto1 =
                ItemRequestWithItemsForThisReqDto.builder()
                        .id(1L)
                        .items(dtoItemList).build();
        ItemRequestWithItemsForThisReqDto itemRequestWithItemsForThisReqDto2 =
                ItemRequestWithItemsForThisReqDto.builder()
                        .id(2L)
                        .items(dtoItemList).build();
        List<ItemRequestWithItemsForThisReqDto> list = List.of(itemRequestWithItemsForThisReqDto1,
                itemRequestWithItemsForThisReqDto2);

        assertEquals(list, itemRequestService.getReqAllWithItemsForThisReq(1L, PageRequest.of(1, 4)));
    }

    @Test
    void getReqById() {
        Item item1 = new Item();
        Item item2 = new Item();
        List<Item> items = List.of(item1, item2);
        ItemRequest itemRequest1 = ItemRequest.builder().id(1L).build();
        when(userService.findUserByIdForValid(anyLong())).thenReturn(new User());
        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(itemRequest1));
        when(itemRepository.findAllByRequestId(anyLong()))
                .thenReturn(items);

        List<ItemForRequestDto> dtoItemList = itemListMapper.modelsToDtosForRequest(items);
        ItemRequestWithItemsForThisReqDto item =
                ItemRequestWithItemsForThisReqDto.builder()
                        .id(1L)
                        .items(dtoItemList).build();

        assertEquals(item, itemRequestService.getReqById(1L, 1L));

    }
}