package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsForThisReqDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRequestClientTest {

    public static MockWebServer server;

    @Autowired
    private ItemRequestClient client;
    @Autowired
    private ObjectMapper mapper;

    @BeforeAll
    @SneakyThrows
    static void setUp() {
        server = new MockWebServer();
        server.start(9090);
    }

    @AfterAll
    @SneakyThrows
    static void afterAll() {
        server.shutdown();
    }

    @Test
    @SneakyThrows
    void create() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().description("Имя").build();
        server.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody(mapper.writeValueAsString(itemRequestDto)));
        ResponseEntity<Object> response = client.create(1L, itemRequestDto);
        RecordedRequest recordedRequest = server.takeRequest();
        assertEquals("POST", recordedRequest.getMethod());
        assertEquals("1", recordedRequest.getHeader("X-Sharer-User-Id"));
        assertEquals(mapper.writeValueAsString(itemRequestDto), recordedRequest.getBody().readUtf8());
        assertEquals("/requests", recordedRequest.getPath());
    }

    @Test
    @SneakyThrows
    void getReqUserWithItemsForThisReq() {
        ItemRequestWithItemsForThisReqDto itemRequestDto = ItemRequestWithItemsForThisReqDto.builder().description("Имя").build();
        server.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody(mapper.writeValueAsString(List.of(itemRequestDto))));
        ResponseEntity<Object> response = client.getReqUserWithItemsForThisReq(1L, 30, 5);
        RecordedRequest recordedRequest = server.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("1", recordedRequest.getHeader("X-Sharer-User-Id"));
        assertEquals("/requests?from=30&size=5", recordedRequest.getPath());
    }

    @Test
    @SneakyThrows
    void getReqAllWithItemsForThisReq() {
        ItemRequestWithItemsForThisReqDto itemRequestDto = ItemRequestWithItemsForThisReqDto.builder().description("Имя").build();
        server.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody(mapper.writeValueAsString(List.of(itemRequestDto))));
        ResponseEntity<Object> response = client.getReqAllWithItemsForThisReq(1L, 30, 5);
        RecordedRequest recordedRequest = server.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("1", recordedRequest.getHeader("X-Sharer-User-Id"));
        assertEquals("/requests/all?from=30&size=5", recordedRequest.getPath());

    }

    @Test
    @SneakyThrows
    void getReqById() {
        ItemRequestWithItemsForThisReqDto itemRequestDto = ItemRequestWithItemsForThisReqDto.builder().description("Имя").build();
        server.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody(mapper.writeValueAsString(List.of(itemRequestDto))));
        ResponseEntity<Object> response = client.getReqById(1L, 5L);
        RecordedRequest recordedRequest = server.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("1", recordedRequest.getHeader("X-Sharer-User-Id"));
        assertEquals("/requests/5", recordedRequest.getPath());

    }
}