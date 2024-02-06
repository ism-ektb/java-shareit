package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.SimpleBookingDto;
import ru.practicum.shareit.exception.NoFoundException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.dto.BookingState.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @MockBean
    private BookingClient client;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;
    BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        bookingDto = BookingDto.builder()
                .item(ItemDto.builder().id(1L).build())
                .build();
    }

    @Test
    @SneakyThrows
    void createBookingDto_whenBookingIsValid_thenSaveAndReturnBooking() {
        long userId = 1L;
        SimpleBookingDto simpleBookingDto = SimpleBookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        when(client.create(anyLong(), any())).thenReturn(ResponseEntity.ok().body(bookingDto));
        String response = mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(simpleBookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(bookingDto), response);
        verify(client).create(userId, simpleBookingDto);
    }

    @Test
    @SneakyThrows
    void createBookingDto_whenBookingIsNotValidStartInPast_thenDontSaveThrowException() {
        long userId = 1L;
        SimpleBookingDto simpleBookingDto = SimpleBookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(simpleBookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is(400));
        verifyNoInteractions(client);
    }

    @Test
    @SneakyThrows
    void createBookingDto_whenBookingIsNotValidStartAfterEnd_thenDontSaveThrowException() {
        long userId = 1L;
        SimpleBookingDto simpleBookingDto = SimpleBookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(5))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(simpleBookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is(400));
        verifyNoInteractions(client);
    }

    @Test
    @SneakyThrows
    void createBookingDto_whenBookingIsNotValidItemIdIsMissing_thenDontSaveThrowException() {
        long userId = 1L;
        SimpleBookingDto simpleBookingDto = SimpleBookingDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(simpleBookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is(400));
        verifyNoInteractions(client);
    }

    @Test
    @SneakyThrows
    void createBookingDto_whenBookingIsNotValidHeaderParamIsMissing_thenDontSaveThrowException() {
        long userId = 1L;
        SimpleBookingDto simpleBookingDto = SimpleBookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        mvc.perform(post("/bookings")
                        //   .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(simpleBookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is(400));
        verifyNoInteractions(client);
    }

    @Test
    @SneakyThrows
    void approved_whenParamsAreValid_thenApprovedAndReturnBooking() {
        when(client.approved(anyLong(), anyLong(), anyBoolean())).thenReturn(ResponseEntity.ok().body(bookingDto));
        String response = mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", "2")
                        .param("approved", "true"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(bookingDto), response);
        verify(client).approved(2L, 1L, true);

    }

    @Test
    @SneakyThrows
    void approved_whenParamsAreNotValidApprovedIsFail_thenDontApprovedBookingAndThrowException() {
        when(client.approved(anyLong(), anyLong(), anyBoolean())).thenReturn(ResponseEntity.ok().body(bookingDto));
        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", "2")
                        .param("approved", "trueeee"))
                .andExpect(status().is(400));
        verifyNoInteractions(client);
    }

    @Test
    @SneakyThrows
    void approved_whenParamsAreNotValidHeaderIsMassing_thenDontApprovedBookingAndThrowException() {
        when(client.approved(anyLong(), anyLong(), anyBoolean())).thenReturn(ResponseEntity.ok().body(bookingDto));
        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        //  .header("X-Sharer-User-Id", "2")
                        .param("approved", "true"))
                .andExpect(status().is(400));
        verifyNoInteractions(client);
    }

    @Test
    @SneakyThrows
    void approved_whenParamsAreNotValidBookingIdIsFail_thenThrowException() {
        when(client.approved(anyLong(), anyLong(), anyBoolean())).thenThrow(new NoFoundException("Бронирование не найдено"));
        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", "2")
                        .param("approved", "true"))
                .andExpect(status().is(404));
        verify(client).approved(2L, 1L, true);
    }

    @Test
    @SneakyThrows
    void getBookingDtoById_whenUserIdIsMissing_thenclientIsNotCallAndThrowException() {
        when(client.getById(anyLong(), anyLong())).thenReturn(ResponseEntity.ok().body(bookingDto));
        mvc.perform(get("/bookings/{bookingId}", 1L))
                //  .header("X-Sharer-User-Id", "2")
                .andExpect(status().is(400));
        verifyNoInteractions(client);
    }

    @Test
    @SneakyThrows
    void getBookingDtoById_whenUserIdIsNotValid_thenThrowException() {
        when(client.getById(anyLong(), anyLong())).thenThrow(new NoFoundException("Юзер не найден"));
        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", "2"))
                .andExpect(status().is(404));
        verify(client).getById(2L, 1L);
    }


    @Test
    @SneakyThrows
    void getBookingDtoByState_whenSendValidData_thenReturnList() {
        List<BookingDto> list = List.of(bookingDto);
        when(client.findAllForBooker(anyLong(), any(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok().body(list));
        String response = mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "2")
                        .param("state", "PAST")
                        .param("from", "30")
                        .param("size", "5"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(list), response);
        verify(client).findAllForBooker(2L, PAST, 30, 5);
    }

    @Test
    @SneakyThrows
    void getBookingDtoByState_whenParamAreMissing_thenReturnList() {
        List<BookingDto> list = List.of(bookingDto);
        when(client.findAllForBooker(anyLong(), any(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok().body(list));
        String response = mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "2"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(list), response);
        verify(client).findAllForBooker(2L, ALL, 0, 10);
    }

    @Test
    @SneakyThrows
    void getBookingDtoByState_whenParamStateIsNotValid_thenclientIsNotCallAndThrowException() {
        List<BookingDto> list = List.of(bookingDto);
        when(client.findAllForBooker(anyLong(), any(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok().body(list));
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "2")
                        .param("state", "PASTTTTT")
                        .param("from", "30")
                        .param("size", "5"))
                .andExpect(status().is(400));
        verifyNoInteractions(client);
    }

    @Test
    @SneakyThrows
    void getBookingDtoByState_whenParamFromIsNotValid_thenServiceIsNotCallAndThrowException() {
        List<BookingDto> list = List.of(bookingDto);
        when(client.findAllForBooker(anyLong(), any(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok().body(list));
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "2")
                        .param("state", "PAST")
                        .param("from", "-30")
                        .param("size", "5"))
                .andExpect(status().is(400));
        verifyNoInteractions(client);
    }

    @Test
    @SneakyThrows
    void getBookingDtoByState_whenHeaderIsMissing_thenServiceIsNotCallAndThrowException() {
        List<BookingDto> list = List.of(bookingDto);
        when(client.findAllForBooker(anyLong(), any(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok().body(list));
        mvc.perform(get("/bookings")
                        //    .header("X-Sharer-User-Id", "2")
                        .param("state", "PAST")
                        .param("from", "30")
                        .param("size", "5"))
                .andExpect(status().is(400));
        verifyNoInteractions(client);
    }

    @Test
    @SneakyThrows
    void getBookingDtoByState_whenHeaderUserIdIsNotValid_thenThrowException() {
        List<BookingDto> list = List.of(bookingDto);
        when(client.findAllForBooker(anyLong(), any(), anyInt(), anyInt())).thenThrow(new NoFoundException("Юзер отсутствует"));
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "2")
                        .param("state", "PAST")
                        .param("from", "30")
                        .param("size", "5"))
                .andExpect(status().is(404));
        verify(client).findAllForBooker(2L, PAST, 30, 5);
    }


    @Test
    @SneakyThrows
    void getBookingDtoByOwner_whenSendValidData_thenReturnList() {
        List<BookingDto> list = List.of(bookingDto);
        when(client.findAllForOwner(anyLong(), any(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok().body(list));
        String response = mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "2")
                        .param("state", "PAST")
                        .param("from", "30")
                        .param("size", "5"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(list), response);
        verify(client).findAllForOwner(2L, PAST, 30, 5);
    }

    @Test
    @SneakyThrows
    void getBookingDtoByOwner_whenParamAreMissing_thenReturnList() {
        List<BookingDto> list = List.of(bookingDto);
        when(client.findAllForOwner(anyLong(), any(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok().body(list));
        String response = mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "2"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(list), response);
        verify(client).findAllForOwner(2L, ALL, 0, 10);
    }

    @Test
    @SneakyThrows
    void getBookingDtoByOwner_whenParamStateIsNotValid_thenclientIsNotCallAndThrowException() {
        List<BookingDto> list = List.of(bookingDto);
        when(client.findAllForOwner(anyLong(), any(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok().body(list));
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "2")
                        .param("state", "PASTTTTT")
                        .param("from", "30")
                        .param("size", "5"))
                .andExpect(status().is(400));
        verifyNoInteractions(client);
    }

    @Test
    @SneakyThrows
    void getBookingDtoByOwner_whenParamFromIsNotValid_thenServiceIsNotCallAndThrowException() {
        List<BookingDto> list = List.of(bookingDto);
        when(client.findAllForOwner(anyLong(), any(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok().body(list));
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "2")
                        .param("state", "PAST")
                        .param("from", "-30")
                        .param("size", "5"))
                .andExpect(status().is(400));
        verifyNoInteractions(client);
    }

    @Test
    @SneakyThrows
    void getBookingDtoByOwner_whenHeaderIsMissing_thenServiceIsNotCallAndThrowException() {
        List<BookingDto> list = List.of(bookingDto);
        when(client.findAllForOwner(anyLong(), any(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok().body(list));
        mvc.perform(get("/bookings/owner")
                        //    .header("X-Sharer-User-Id", "2")
                        .param("state", "PAST")
                        .param("from", "30")
                        .param("size", "5"))
                .andExpect(status().is(400));
        verifyNoInteractions(client);
    }
}