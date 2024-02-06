package ru.practicum.shareit.request;

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
import ru.practicum.shareit.exception.NoFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsForThisReqDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemRequestService service;
    @Autowired
    private MockMvc mvc;

    private ItemRequestWithItemsForThisReqDto itemRequestWithItemsForThisReqDto;

    @BeforeEach
    void before() {
        itemRequestWithItemsForThisReqDto = ItemRequestWithItemsForThisReqDto
                .builder()
                .description("Описание")
                .build();
    }

    @SneakyThrows
    @Test
    void createRequest_whenReqIsValid_thenReturnReq() {
        long userId = 1L;
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("Описание")
                .build();
        when(service.create(anyLong(), any())).thenReturn(itemRequestDto);

        String result = mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(itemRequestDto), result);
        verify(service).create(userId, itemRequestDto);
    }

    @SneakyThrows
    @Test
    void createRequest_whenReqIsNotValid_thenReturnBedRequest() {
        long userId = 1L;
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("   ")
                .build();
        when(service.create(anyLong(), any())).thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));

        verify(service, never()).create(userId, itemRequestDto);
    }

    @SneakyThrows
    @Test
    void createRequest_whenHeaderIsNotValid_thenReturnBedRequest() {
        long userId = 1L;
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("Описание")
                .build();
        when(service.create(anyLong(), any())).thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));

        verify(service, never()).create(userId, itemRequestDto);
    }

    @SneakyThrows
    @Test
    void getUserRequestsWithItem_whenRequestIsValid_whenReturnList() {
        long userId = 1L;
        when(service.getReqUserWithItemsForThisReq(anyLong(), any()))
                .thenReturn(List.of(itemRequestWithItemsForThisReqDto));

        String result = mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "10")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(List.of(itemRequestWithItemsForThisReqDto)), result);

        verify(service).getReqUserWithItemsForThisReq(userId, PageRequest.of(10 / 3, 3));
    }

    @SneakyThrows
    @Test
    void getUserRequestsWithItem_whenRequestIsValidWithoutParam_whenReturnList() {
        long userId = 1L;
        when(service.getReqUserWithItemsForThisReq(anyLong(), any()))
                .thenReturn(List.of(itemRequestWithItemsForThisReqDto));

        String result = mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(List.of(itemRequestWithItemsForThisReqDto)), result);

        verify(service).getReqUserWithItemsForThisReq(userId, PageRequest.of(0 / 10, 10));
    }

    @SneakyThrows
    @Test
    void getUserRequestsWithItem_whenHeaderIsNotValid_whenBadRequest() {
        long userId = 1L;
        when(service.getReqUserWithItemsForThisReq(anyLong(), any()))
                .thenReturn(List.of(itemRequestWithItemsForThisReqDto));

        mvc.perform(get("/requests")
                        .param("from", "10")
                        .param("size", "3"))
                .andExpect(status().is(400));
    }

    @SneakyThrows
    @Test
    void getUserRequestsWithItem_whenParamIsNotValid_whenBadRequest() {
        long userId = 1L;
        when(service.getReqUserWithItemsForThisReq(anyLong(), any()))
                .thenReturn(List.of(itemRequestWithItemsForThisReqDto));

        mvc.perform(get("/requests")
                        .param("from", "10")
                        .param("size", "0"))
                .andExpect(status().is(400));
    }

    @SneakyThrows
    @Test
    void getAllRequestsWithItem_whenRequestIsValid_whenReturnList() {
        long userId = 1L;
        when(service.getReqAllWithItemsForThisReq(anyLong(), any()))
                .thenReturn(List.of(itemRequestWithItemsForThisReqDto));

        String result = mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "10")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(List.of(itemRequestWithItemsForThisReqDto)), result);

        verify(service).getReqAllWithItemsForThisReq(userId, PageRequest.of(10 / 3, 3));
    }

    @SneakyThrows
    @Test
    void getRequestById_whenRequestByIdNotFound_thenReturnNoFound() {
        long requestId = 10;
        when(service.getReqById(anyLong(), anyLong())).thenThrow(new NoFoundException("ничего не найдено"));

        mvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is(404));
    }

    @SneakyThrows
    @Test
    void getRequestById_whenRequestIdValid_thenReturnRequestDto() {
        long requestId = 10;
        when(service.getReqById(anyLong(), anyLong())).thenReturn(itemRequestWithItemsForThisReqDto);

        String result = mvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is(200))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(itemRequestWithItemsForThisReqDto), result);

        verify(service).getReqById(1L, requestId);
    }

    @SneakyThrows
    @Test
    void getRequestById_whenRequestWithoutHeaderParam_thenReturnBadRequest() {
        long requestId = 10;
        when(service.getReqById(anyLong(), anyLong())).thenReturn(itemRequestWithItemsForThisReqDto);

        mvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().is(400));
    }
}