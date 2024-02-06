package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserClientTest {

    public static MockWebServer mockBackEnd;

    @Autowired
    private UserClient userClient;
    @Autowired
    private ObjectMapper mapper;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start(9090);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }


    @Test
    @SneakyThrows
    void createUser() {
        UserDto userDto = UserDto.builder().name("Имя").build();
        mockBackEnd.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(userDto))
                .addHeader("Content-Type", "application/json"));

        ResponseEntity<Object> response = userClient.createUser(userDto);

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();

        assertEquals("POST", recordedRequest.getMethod());
        assertEquals("/users", recordedRequest.getPath());
        assertEquals(mapper.writeValueAsString(userDto), recordedRequest.getBody().readUtf8());
    }

    @Test
    @SneakyThrows
    void findUserById() {
        UserDto userDto = UserDto.builder().name("Имя").build();

        mockBackEnd.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(userDto))
                .addHeader("Content-Type", "application/json"));
        ResponseEntity<Object> response = userClient.findUserById(1L);
        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/users/1", recordedRequest.getPath());
    }

    @Test
    @SneakyThrows
    void findAllUsers() {
        UserDto userDto = UserDto.builder().name("Имя").build();

        mockBackEnd.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(List.of(userDto)))
                .addHeader("Content-Type", "application/json"));
        ResponseEntity<Object> response = userClient.findAllUsers();
        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/users", recordedRequest.getPath());
    }

    @Test
    @SneakyThrows
    void updateUser() {
        UserDto userDto = UserDto.builder().name("Имя").build();

        mockBackEnd.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(userDto))
                .addHeader("Content-Type", "application/json"));
        ResponseEntity<Object> response = userClient.updateUser(1L, userDto);
        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("PATCH", recordedRequest.getMethod());
        assertEquals("/users/1", recordedRequest.getPath());
        assertEquals(mapper.writeValueAsString(userDto), recordedRequest.getBody().readUtf8());
    }

    @Test
    @SneakyThrows
    void deleteUser() {

        mockBackEnd.enqueue(new MockResponse()
                 .addHeader("Content-Type", "application/json"));
        ResponseEntity response = userClient.deleteUser(1L);
        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("DELETE", recordedRequest.getMethod());
        assertEquals("/users/1", recordedRequest.getPath());
    }
}