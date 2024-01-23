package ru.practicum.shareit.booking.dtoMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BookingMapperTest {

    @Autowired
    private BookingMapper mapper;

    @Test
    void modelToDtoForItem() {
        Booking booking = Booking.builder()
                .id(1L)
                .booker(User.builder().id(5L).name("name").build())
                .build();
        BookingForItemDto bookingForItemDto = BookingForItemDto.builder()
                .id(1L)
                .bookerId(5L).build();
        assertEquals(bookingForItemDto, mapper.modelToDtoForItem(booking));
    }
}