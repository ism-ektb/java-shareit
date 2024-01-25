package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.SimpleBookingDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class BookingControllerUnitTest {

    @MockBean
    private BookingService service;
    @Autowired
    private BookingController controller;


    @Test
    void createBookingDto() {
        SimpleBookingDto simpleBookingDto = SimpleBookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        BookingDto bookingDto = BookingDto.builder().id(1L).build();
        when(service.create(anyLong(), any())).thenReturn(bookingDto);
        assertEquals(controller.createBookingDto(1L, simpleBookingDto), bookingDto);
    }

    @Test
    void approved() {
        BookingDto bookingDto = BookingDto.builder().id(1L).build();
        when(service.approved(anyLong(), anyLong(), any())).thenReturn(bookingDto);
        assertEquals(controller.approved(1L, 1L, true), bookingDto);
    }

    @Test
    void getBookingDtoById() {
    }

    @Test
    void getBookingDtoByState() {
    }

    @Test
    void getBookingDtoByOwner() {
    }
}