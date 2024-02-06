package ru.practicum.shareit.item;

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
import ru.practicum.shareit.comment.dto.CommentInDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemClientTest {

    public static MockWebServer mockBackEnd;

    @Autowired
    private ItemClient client;

    @Autowired
    private ObjectMapper mapper;

    @BeforeAll
    @SneakyThrows
    static void setUp() {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start(9090);
    }

    @AfterAll
    @SneakyThrows
    static void tearDown() {
        mockBackEnd.shutdown();
    }

    @Test
    @SneakyThrows
    void createItem() {
        ItemDto itemDto = ItemDto.builder().name("Имя").build();
        mockBackEnd.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(itemDto))
                .addHeader("Content-Type", "application/json"));

        ResponseEntity<Object> response = client.createItem(itemDto, 1L);

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();

        assertEquals("POST", recordedRequest.getMethod());
        assertEquals("/items", recordedRequest.getPath());
        assertEquals("1", recordedRequest.getHeader("X-Sharer-User-Id"));
        assertEquals(mapper.writeValueAsString(itemDto), recordedRequest.getBody().readUtf8());
    }

    @Test
    @SneakyThrows
    void updateItem() {
        ItemDto itemDto = ItemDto.builder().name("Имя").build();
        mockBackEnd.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(itemDto))
                .addHeader("Content-Type", "application/json"));

        ResponseEntity<Object> response = client.updateItem(1L, 2L, itemDto);

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();

        assertEquals("PATCH", recordedRequest.getMethod());
        assertEquals("/items/2", recordedRequest.getPath());
        assertEquals("1", recordedRequest.getHeader("X-Sharer-User-Id"));
        assertEquals(mapper.writeValueAsString(itemDto), recordedRequest.getBody().readUtf8());

    }

    @Test
    @SneakyThrows
    void getItemOfId() {
        ItemDto itemDto = ItemDto.builder().name("Имя").build();
        mockBackEnd.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(itemDto))
                .addHeader("Content-Type", "application/json"));

        ResponseEntity<Object> response = client.getItemOfId(1L, 2L);

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();

        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/items/2", recordedRequest.getPath());
        assertEquals("1", recordedRequest.getHeader("X-Sharer-User-Id"));
    }

    @Test
    @SneakyThrows
    void getItems() {
        ItemDto itemDto = ItemDto.builder().name("Имя").build();
        mockBackEnd.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(itemDto))
                .addHeader("Content-Type", "application/json"));

        ResponseEntity<Object> response = client.getItems(1L, 30, 6);

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();

        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/items?from=30&size=6", recordedRequest.getPath());
        assertEquals("1", recordedRequest.getHeader("X-Sharer-User-Id"));

    }

    @Test
    @SneakyThrows
    void getItemOfText() {
        ItemDto itemDto = ItemDto.builder().name("Имя").build();
        mockBackEnd.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(List.of(itemDto)))
                .addHeader("Content-Type", "application/json"));

        ResponseEntity<Object> response = client.getItemOfText(1L, "text", 30, 6);

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();

        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/items/search?text=text&from=30&size=6", recordedRequest.getPath());
        assertEquals("1", recordedRequest.getHeader("X-Sharer-User-Id"));
    }

    @Test
    @SneakyThrows
    void addComment() {
        CommentInDto commentInDto = CommentInDto.builder().text("Текст").build();
        mockBackEnd.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(commentInDto))
                .addHeader("Content-Type", "application/json"));

        ResponseEntity<Object> response = client.addComment(1L, 2L, commentInDto);

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();

        assertEquals("POST", recordedRequest.getMethod());
        assertEquals("/items/2/comment", recordedRequest.getPath());
        assertEquals("1", recordedRequest.getHeader("X-Sharer-User-Id"));
    }
}