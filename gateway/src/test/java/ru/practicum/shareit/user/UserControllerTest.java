package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @MockBean
    private UserClient client;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    private long userId = 1L;


    @Test
    @SneakyThrows
    void createUserDto_whenDataIsValid_thenSaveUserAndReturnUser() {
        UserDto userDto = UserDto.builder()
                .name("имя")
                .email("e@e.e").build();
        when(client.createUser(any())).thenReturn(ResponseEntity.ok().body(userDto));
        String response = mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(userDto), response);
        verify(client).createUser(userDto);
    }

    @Test
    @SneakyThrows
    void createUserDto_whenEmailIsNotValid_thenDontSaveUserAndThrowException() {
        UserDto userDto = UserDto.builder()
                .name("имя")
                .email("ee.e").build();
        when(client.createUser(any())).thenReturn(ResponseEntity.ok().body(userDto));
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
        verifyNoInteractions(client);
    }

    @Test
    @SneakyThrows
    void createUserDto_whenNameIsBlankValid_thenSaveUserAndReturnUser() {
        UserDto userDto = UserDto.builder()
                .name("  ")
                .email("e@e.e").build();
        when(client.createUser(any())).thenReturn(ResponseEntity.ok().body(userDto));
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
        verifyNoInteractions(client);
    }

    @Test
    @SneakyThrows
    void getUserDtoById_whenDataValid_thenReturnUserId() {
        UserDto userDto = UserDto.builder()
                .id(1L).build();
        when(client.findUserById(anyLong())).thenReturn(ResponseEntity.ok().body(userDto));
        String response = mvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(userDto), response);
        verify(client).findUserById(1L);
    }

    @SneakyThrows
    @Test
    void getUserDtoById_whenIdIsValid_thenReturnUserId() {
        UserDto userDto = UserDto.builder()
                .id(1L).build();
        when(client.findUserById(anyLong())).thenReturn(ResponseEntity.ok().body(userDto));
        mvc.perform(get("/users/{id}", "W"))
                .andExpect(status().is(400));
        verifyNoInteractions(client);
    }

    @Test
    @SneakyThrows
    void getUserDtos_whenDataValid_thenReturnList() {
        UserDto userDto = UserDto.builder()
                .id(1L).build();
        List<UserDto> list = List.of(userDto);
        when(client.findAllUsers()).thenReturn(ResponseEntity.ok().body(list));
        String response = mvc.perform(get("/users"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(list), response);
        verify(client).findAllUsers();
    }

    @Test
    @SneakyThrows
    void updateUserDto_whenDataIsValid_thenSaveDataAndReturnUser() {
        UserDto userDto = UserDto.builder()
                .email("e@e.e").build();
        when(client.updateUser(anyLong(), any())).thenReturn(ResponseEntity.ok().body(userDto));
        String response = mvc.perform(patch("/users/{id}", 1L)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(userDto), response);
        verify(client).updateUser(1L, userDto);
    }

    @Test
    @SneakyThrows
    void updateUserDto_whenEmailIsNotValid_thenDontSaveDataAndThrowException() {
        UserDto userDto = UserDto.builder()
                .email("ee.e").build();
        when(client.updateUser(anyLong(), any())).thenReturn(ResponseEntity.ok().body(userDto));
        mvc.perform(patch("/users/{id}", 1L)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
        verifyNoInteractions(client);
    }
}