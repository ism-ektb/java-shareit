package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentInDto;
import ru.practicum.shareit.comment.dto.CommentOutDto;
import ru.practicum.shareit.exception.NoFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemService service;

    ItemDto itemDto;
    long userId;
    long itemId;
    ItemWithBookingAndCommentDto itemWithBookingAndCommentDto;
    List<ItemWithBookingAndCommentDto> list;
    List<ItemDto> dtoList;

    @BeforeEach
    void appEach() {
        itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();
        itemWithBookingAndCommentDto = ItemWithBookingAndCommentDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();
        list = List.of(itemWithBookingAndCommentDto);
        dtoList = List.of(itemDto);
        userId = 1L;
        itemId = 3L;

    }

    @Test
    @SneakyThrows
    void createItemDto_whenItemIsValid_thenReturnItem() {
        when(service.createItem(any(), anyLong())).thenReturn(itemDto);
        String response = mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200)).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(itemDto), response);
        verify(service).createItem(itemDto, userId);
    }

    @Test
    @SneakyThrows
    void createItemDto_whenParamIsNotValid_thenReturnBedRequest() {
        String badUserId = "tyu";
        when(service.createItem(any(), anyLong())).thenReturn(itemDto);
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", badUserId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
        verifyNoInteractions(service);
    }

    @Test
    @SneakyThrows
    void createItemDto_whenItemIsNotValid_thenReturnBedRequest() {
        itemDto.setDescription("   ");
        when(service.createItem(any(), anyLong())).thenReturn(itemDto);
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
        verifyNoInteractions(service);
    }


    @Test
    @SneakyThrows
    void updateItemDto_whenItemIsValid_thenReturnItem() {
        when(service.updateItem(anyLong(), anyLong(), any())).thenReturn(itemDto);
        String response = mvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(itemDto), response);
        verify(service).updateItem(userId, itemId, itemDto);
    }

    @Test
    @SneakyThrows
    void updateItemDto_whenHeaderParamIsNotValid_thenReturnBadRequest() {
        when(service.updateItem(anyLong(), anyLong(), any())).thenReturn(itemDto);
        mvc.perform(patch("/items/{id}", itemId)
                        //    .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
        verifyNoInteractions(service);
    }

    @Test
    @SneakyThrows
    void updateItemDto_whenItemIdIsNotFound_thenReturnNotFound() {
        when(service.updateItem(anyLong(), anyLong(), any())).thenThrow(new NoFoundException("Вещь не найдена"));
        mvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    @SneakyThrows
    void getItemDtoById_whenRequestIsValid_thenReturnItemDto() {
        when(service.getItemOfId(anyLong(), anyLong())).thenReturn(itemWithBookingAndCommentDto);
        String response = mvc.perform(get("/items/{itemsId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(itemWithBookingAndCommentDto), response);
        verify(service).getItemOfId(userId, itemId);
    }

    @Test
    @SneakyThrows
    void getItemDtoById_whenItemIdIsNotFound_thenReturnNotFound() {
        when(service.getItemOfId(anyLong(), anyLong())).thenThrow(new NoFoundException("Ничено не найдено"));
        mvc.perform(get("/items/{itemsId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().is(404));
    }

    @Test
    @SneakyThrows
    void getItemDtoById_whenHeaderParamIsNotValid_thenReturnBadRequest() {
        when(service.getItemOfId(anyLong(), anyLong())).thenReturn(itemWithBookingAndCommentDto);
        mvc.perform(get("/items/{itemsId}", itemId))
                //  .header("X-Sharer-User-Id", userId))
                .andExpect(status().is(400));
        verifyNoInteractions(service);
    }

    @Test
    @SneakyThrows
    void getItemDtoByUser_whenRequestIsValidAndNoParam_thenReturnListItemDto() {
        when(service.getItems(anyLong(), any())).thenReturn(list);
        String response = mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(list), response);
        verify(service).getItems(userId, PageRequest.of(0, 10));
    }

    @Test
    @SneakyThrows
    void getItemDtoByUser_whenRequestIsValidAndWithParam_thenReturnListItemDto() {
        when(service.getItems(anyLong(), any())).thenReturn(list);
        String response = mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "30")
                        .param("size", "5"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(list), response);
        verify(service).getItems(userId, PageRequest.of(6, 5));
    }

    @Test
    @SneakyThrows
    void getItemDtoByUser_whenRequestIsNotValidWithFailParam_thenReturnBadRequest() {
        when(service.getItems(anyLong(), any())).thenReturn(list);
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "-30")
                        .param("size", "5"))
                .andExpect(status().is(400));
        verifyNoInteractions(service);
    }

    @Test
    @SneakyThrows
    void getItemDtoByUser_whenUserNotFound_thenReturnNotFound() {
        when(service.getItems(anyLong(), any())).thenThrow(new NoFoundException("Юзер не найден"));
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().is(404));
        verify(service).getItems(userId, PageRequest.of(0, 10));
    }

    @Test
    @SneakyThrows
    void getItemDtoByUser_whenRequestIsNotValid_thenReturnBadRequest() {
        when(service.getItems(anyLong(), any())).thenReturn(list);
        mvc.perform(get("/items"))
                //        .header("X-Sharer-User-Id", userId))
                .andExpect(status().is(400));
        verifyNoInteractions(service);
    }

    @Test
    @SneakyThrows
    void searchItemDtoByUser_whenRequestIsValidAndNoParamPage_thenReturnListItemDto() {
        when(service.getItemOfText(anyLong(), anyString(), any())).thenReturn(dtoList);
        String response = mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", "example"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(dtoList), response);
        verify(service).getItemOfText(userId, "example", PageRequest.of(0, 10));
    }

    @Test
    @SneakyThrows
    void searchItemDtoByUser_whenRequestIsValidAndNoWithParamPage_thenReturnListItemDto() {
        when(service.getItemOfText(anyLong(), anyString(), any())).thenReturn(dtoList);
        String response = mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", "example")
                        .param("from", "30")
                        .param("size", "5"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(dtoList), response);
        verify(service).getItemOfText(userId, "example", PageRequest.of(6, 5));
    }

    @Test
    @SneakyThrows
    void searchItemDtoByUser_whenRequestIsValidAndNoWithParamPageNotText_thenReturnEmptyList() {
        when(service.getItemOfText(anyLong(), anyString(), any())).thenReturn(new ArrayList<>());
        String response = mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "30")
                        .param("size", "5"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(new ArrayList<>()), response);
        verify(service).getItemOfText(userId, "", PageRequest.of(6, 5));
    }

    @Test
    @SneakyThrows
    void searchItemDtoByUser_whenRequestIsNotValidWithoutUserId_thenReturnBadRequest() {
        when(service.getItemOfText(anyLong(), anyString(), any())).thenReturn(dtoList);
        mvc.perform(get("/items/search"))
                .andExpect(status().is(400));
        verifyNoInteractions(service);
    }

    @Test
    @SneakyThrows
    void searchItemDtoByUser_whenRequestIsValidBatUserNotFound_thenReturnNotFound() {
        when(service.getItemOfText(anyLong(), anyString(), any())).thenThrow(new NoFoundException("Юзер отсутствует"));
        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", "example"))
                .andExpect(status().is(404));
        verify(service).getItemOfText(userId, "example", PageRequest.of(0, 10));
    }

    @Test
    @SneakyThrows
    void searchItemDtoByUser_whenRequestIsNotValidFailWithParamPage_thenReturnBadRequest() {
        when(service.getItemOfText(anyLong(), anyString(), any())).thenReturn(dtoList);
        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", "example")
                        .param("from", "-30")
                        .param("size", "5"))
                .andExpect(status().is(400));
        verifyNoInteractions(service);
    }

    @Test
    @SneakyThrows
    void addCommentForItem_whenRequestIsValid_thenReturnComment() {
        CommentInDto commentInDto = CommentInDto.builder().text("текст").build();
        CommentOutDto commentOutDto = CommentOutDto.builder().text("текст").build();
        when(service.addComment(anyLong(), anyLong(), any())).thenReturn(commentOutDto);
        String response = mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(commentInDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200)).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(commentOutDto), response);
        verify(service).addComment(userId, itemId, commentInDto);
    }

    @Test
    @SneakyThrows
    void addCommentForItem_whenCommentIsValid_thenBadRequest() {
        CommentInDto commentInDto = CommentInDto.builder().text("   ").build();
        CommentOutDto commentOutDto = CommentOutDto.builder().text("текст").build();
        when(service.addComment(anyLong(), anyLong(), any())).thenReturn(commentOutDto);
        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(commentInDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
        verifyNoInteractions(service);
    }

    @Test
    @SneakyThrows
    void addCommentForItem_whenServiceThrowException_thenBadRequest() {
        CommentInDto commentInDto = CommentInDto.builder().text("текст").build();
        CommentOutDto commentOutDto = CommentOutDto.builder().text("текст").build();
        when(service.addComment(anyLong(), anyLong(), any())).thenThrow(new ValidationException("Ошибка валидации"));
        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(commentInDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
        verify(service).addComment(userId, itemId, commentInDto);
    }

    @Test
    @SneakyThrows
    void addCommentForItem_whenCommentIsValidFailParam_thenBadRequest() {
        CommentInDto commentInDto = CommentInDto.builder().text("текст").build();
        CommentOutDto commentOutDto = CommentOutDto.builder().text("текст").build();
        when(service.addComment(anyLong(), anyLong(), any())).thenReturn(commentOutDto);
        mvc.perform(post("/items/{itemId}/comment", itemId)
                        //   .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(commentInDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
        verifyNoInteractions(service);
    }
}