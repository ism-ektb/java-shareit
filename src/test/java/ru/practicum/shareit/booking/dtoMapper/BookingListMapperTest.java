package ru.practicum.shareit.booking.dtoMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookingListMapperTest {

    @Autowired
    private BookingListMapper mapper;

    @Test
    void modelsToDtos() {
        Booking booking = Booking.builder().id(1L).build();
        BookingDto bookingDto = BookingDto.builder().id(1L).build();
        assertEquals(mapper.modelsToDtos(List.of(booking)), List.of(bookingDto));

    }

    @Test
    void dtosToModels() {
        Booking booking = Booking.builder().id(1L).build();
        BookingDto bookingDto = BookingDto.builder().id(1L).build();
        assertEquals(mapper.dtosToModels(List.of(bookingDto)), List.of(booking));

    }
}