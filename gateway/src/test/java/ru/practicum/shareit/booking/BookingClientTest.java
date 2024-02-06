package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.SimpleBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BookingClientTest {

    @Autowired
    private BookingClient client;

    public static MockWebServer server;
    @Autowired
    private ObjectMapper mapper;
    private static BookingDto bookingDto;

    @SneakyThrows
    @BeforeAll
    static void setUp() {
        server = new MockWebServer();
        server.start(9090);
        bookingDto = BookingDto.builder().item(ItemDto.builder().id(1L).build()).build();
    }

    @AfterAll
    @SneakyThrows
    static void tearDown() {
        server.shutdown();
    }

    @Test
    @SneakyThrows
    void findAllForBooker() {
        server.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(List.of(bookingDto)))
                .addHeader("Content-Type", "application/json"));
        ResponseEntity<Object> response = client.findAllForBooker(1L, BookingState.ALL, 30, 5);
        RecordedRequest recordedRequest = server.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("1", recordedRequest.getHeader("X-Sharer-User-Id"));
        assertEquals("/bookings?state=ALL&from=30&size=5", recordedRequest.getPath());
    }

    @Test
    @SneakyThrows
    void findAllForOwner() {
        server.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(List.of(bookingDto)))
                .addHeader("Content-Type", "application/json"));
        ResponseEntity<Object> response = client.findAllForOwner(1L, BookingState.ALL, 30, 5);
        RecordedRequest recordedRequest = server.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("1", recordedRequest.getHeader("X-Sharer-User-Id"));
        assertEquals("/bookings/owner?state=ALL&from=30&size=5", recordedRequest.getPath());
    }

    @Test
    @SneakyThrows
    void getById() {
        server.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString((bookingDto)))
                .addHeader("Content-Type", "application/json"));
        ResponseEntity<Object> response = client.getById(1L, 5L);
        RecordedRequest recordedRequest = server.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("1", recordedRequest.getHeader("X-Sharer-User-Id"));
        assertEquals("/bookings/5", recordedRequest.getPath());
    }

    @Test
    @SneakyThrows
    void create() {
        SimpleBookingDto simpleBookingDto = SimpleBookingDto.builder().itemId(3L).build();
        server.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString((bookingDto)))
                .addHeader("Content-Type", "application/json"));
        ResponseEntity<Object> response = client.create(1L, simpleBookingDto);
        RecordedRequest recordedRequest = server.takeRequest();
        assertEquals("POST", recordedRequest.getMethod());
        assertEquals("1", recordedRequest.getHeader("X-Sharer-User-Id"));
        assertEquals("/bookings", recordedRequest.getPath());
        assertEquals(mapper.writeValueAsString(simpleBookingDto), recordedRequest.getBody().readUtf8());
    }

    @Test
    @SneakyThrows
    void approved() {
        server.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString((bookingDto)))
                .addHeader("Content-Type", "application/json"));
        ResponseEntity<Object> response = client.approved(1L, 1L, Boolean.TRUE);
        RecordedRequest recordedRequest = server.takeRequest();
        assertEquals("PATCH", recordedRequest.getMethod());
        assertEquals("1", recordedRequest.getHeader("X-Sharer-User-Id"));
        assertEquals("/bookings/1?approved=true", recordedRequest.getPath());
    }
}