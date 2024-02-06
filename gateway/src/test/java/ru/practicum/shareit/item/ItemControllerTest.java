package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentInDto;
import ru.practicum.shareit.comment.dto.CommentOutDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentDto;

import java.nio.charset.StandardCharsets;
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
    private ItemClient client;

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
        when(client.createItem(any(), anyLong())).thenReturn(ResponseEntity.ok().body(itemDto));
        String response = mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200)).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(itemDto), response);
        verify(client).createItem(itemDto, userId);
    }

    @Test
    @SneakyThrows
    void createItemDto_whenParamIsNotValid_thenReturnBedRequest() {
        String badUserId = "tyu";
        when(client.createItem(any(), anyLong())).thenReturn(ResponseEntity.ok().body(itemDto));
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", badUserId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
        verifyNoInteractions(client);
    }

    @Test
    @SneakyThrows
    void createItemDto_whenItemIsNotValid_thenReturnBedRequest() {
        itemDto.setDescription("   ");
        when(client.createItem(any(), anyLong())).thenReturn(ResponseEntity.ok().body(itemDto));
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
        verifyNoInteractions(client);
    }


    @Test
    @SneakyThrows
    void updateItemDto_whenItemIsValid_thenReturnItem() {
        when(client.updateItem(anyLong(), anyLong(), any())).thenReturn(ResponseEntity.ok().body(itemDto));
        String response = mvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(itemDto), response);
        verify(client).updateItem(userId, itemId, itemDto);
    }

    @Test
    @SneakyThrows
    void updateItemDto_whenHeaderParamIsNotValid_thenReturnBadRequest() {
        when(client.updateItem(anyLong(), anyLong(), any())).thenReturn(ResponseEntity.ok().body(itemDto));
        mvc.perform(patch("/items/{id}", itemId)
                        //    .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
        verifyNoInteractions(client);
    }

    @Test
    @SneakyThrows
    void getItemDtoById_whenRequestIsValid_thenReturnItemDto() {
        when(client.getItemOfId(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().body(itemWithBookingAndCommentDto));
        String response = mvc.perform(get("/items/{itemsId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(itemWithBookingAndCommentDto), response);
        verify(client).getItemOfId(userId, itemId);
    }

    @Test
    @SneakyThrows
    void getItemDtoById_whenHeaderParamIsNotValid_thenReturnBadRequest() {
        when(client.getItemOfId(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().body(itemWithBookingAndCommentDto));
        mvc.perform(get("/items/{itemsId}", itemId))
                //  .header("X-Sharer-User-Id", userId))
                .andExpect(status().is(400));
        verifyNoInteractions(client);
    }

    @Test
    @SneakyThrows
    void getItemDtoByUser_whenRequestIsValidAndNoParam_thenReturnListItemDto() {
        when(client.getItems(anyLong(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok().body(list));
        String response = mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(list), response);
        verify(client).getItems(userId, 0, 10);
    }

    @Test
    @SneakyThrows
    void getItemDtoByUser_whenRequestIsValidAndWithParam_thenReturnListItemDto() {
        when(client.getItems(anyLong(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok().body(list));
        String response = mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "30")
                        .param("size", "5"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(list), response);
        verify(client).getItems(userId, 30, 5);
    }

    @Test
    @SneakyThrows
    void getItemDtoByUser_whenRequestIsNotValidWithFailParam_thenReturnBadRequest() {
        when(client.getItems(anyLong(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok().body(list));
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "-30")
                        .param("size", "5"))
                .andExpect(status().is(400));
        verifyNoInteractions(client);
    }

    @Test
    @SneakyThrows
    void getItemDtoByUser_whenRequestIsNotValid_thenReturnBadRequest() {
        when(client.getItems(anyLong(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok().body(list));
        mvc.perform(get("/items"))
                //        .header("X-Sharer-User-Id", userId))
                .andExpect(status().is(400));
        verifyNoInteractions(client);
    }

    @Test
    @SneakyThrows
    void searchItemDtoByUser_whenRequestIsValidAndNoParamPage_thenReturnListItemDto() {
        when(client.getItemOfText(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok().body(dtoList));
        String response = mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", "example"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(dtoList), response);
        verify(client).getItemOfText(userId, "example", 0, 10);
    }

    @Test
    @SneakyThrows
    void searchItemDtoByUser_whenRequestIsValidAndNoWithParamPage_thenReturnListItemDto() {
        when(client.getItemOfText(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok().body(dtoList));
        String response = mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", "example")
                        .param("from", "30")
                        .param("size", "5"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(dtoList), response);
        verify(client).getItemOfText(userId, "example", 30, 5);
    }

    @Test
    @SneakyThrows
    void searchItemDtoByUser_whenRequestIsNotValidWithoutUserId_thenReturnBadRequest() {
        when(client.getItemOfText(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok().body(dtoList));
        mvc.perform(get("/items/search"))
                .andExpect(status().is(400));
        verifyNoInteractions(client);
    }

    @Test
    @SneakyThrows
    void searchItemDtoByUser_whenRequestIsNotValidFailWithParamPage_thenReturnBadRequest() {
        when(client.getItemOfText(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok().body(dtoList));
        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", "example")
                        .param("from", "-30")
                        .param("size", "5"))
                .andExpect(status().is(400));
        verifyNoInteractions(client);
    }

    @Test
    @SneakyThrows
    void addCommentForItem_whenRequestIsValid_thenReturnComment() {
        CommentInDto commentInDto = CommentInDto.builder().text("текст").build();
        CommentOutDto commentOutDto = CommentOutDto.builder().text("текст").build();
        when(client.addComment(anyLong(), anyLong(), any())).thenReturn(ResponseEntity.ok().body(commentOutDto));
        String response = mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(commentInDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200)).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(commentOutDto), response);
        verify(client).addComment(userId, itemId, commentInDto);
    }

    @Test
    @SneakyThrows
    void addCommentForItem_whenCommentIsValid_thenBadRequest() {
        CommentInDto commentInDto = CommentInDto.builder().text("   ").build();
        CommentOutDto commentOutDto = CommentOutDto.builder().text("текст").build();
        when(client.addComment(anyLong(), anyLong(), any())).thenReturn(ResponseEntity.ok().body(commentOutDto));
        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(commentInDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
        verifyNoInteractions(client);
    }

    @Test
    @SneakyThrows
    void addCommentForItem_whenCommentIsValidFailParam_thenBadRequest() {
        CommentInDto commentInDto = CommentInDto.builder().text("текст").build();
        CommentOutDto commentOutDto = CommentOutDto.builder().text("текст").build();
        when(client.addComment(anyLong(), anyLong(), any())).thenReturn(ResponseEntity.ok().body(commentOutDto));
        mvc.perform(post("/items/{itemId}/comment", itemId)
                        //   .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(commentInDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
        verifyNoInteractions(client);
    }
}