package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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
    private UserService service;
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
        when(service.createUser(any())).thenReturn(userDto);
        String response = mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(userDto), response);
        verify(service).createUser(userDto);
    }

    @Test
    @SneakyThrows
    void createUserDto_whenEmailIsNotValid_thenSaveUserAndReturnUser() {
        UserDto userDto = UserDto.builder()
                .name("имя")
                .email("ee.e").build();
        when(service.createUser(any())).thenReturn(userDto);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
        verifyNoInteractions(service);
    }

    @Test
    @SneakyThrows
    void createUserDto_whenNameIsBlankValid_thenSaveUserAndReturnUser() {
        UserDto userDto = UserDto.builder()
                .name("  ")
                .email("e@e.e").build();
        when(service.createUser(any())).thenReturn(userDto);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
        verifyNoInteractions(service);
    }

    @Test
    @SneakyThrows
    void getUserDtoById_whenDataValid_thenReturnUserId() {
        UserDto userDto = UserDto.builder()
                .id(1L).build();
        when(service.findUserById(anyLong())).thenReturn(userDto);
        String response = mvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(userDto), response);
        verify(service).findUserById(1L);
    }

    @SneakyThrows
    @Test
    void getUserDtoById_whenIdIsValid_thenReturnUserId() {
        UserDto userDto = UserDto.builder()
                .id(1L).build();
        when(service.findUserById(anyLong())).thenReturn(userDto);
        mvc.perform(get("/users/{id}", "W"))
                .andExpect(status().is(400));
        verifyNoInteractions(service);
    }

    @Test
    @SneakyThrows
    void getUserDtos_whenDataValid_thenReturnList() {
        UserDto userDto = UserDto.builder()
                .id(1L).build();
        List<UserDto> list = List.of(userDto);
        when(service.findAllUsers()).thenReturn(list);
        String response = mvc.perform(get("/users"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(list), response);
        verify(service).findAllUsers();
    }

    @Test
    @SneakyThrows
    void updateUserDto_whenDataIsValid_thenSaveDataAndReturnUser() {
        UserDto userDto = UserDto.builder()
                .email("e@e.e").build();
        when(service.updateUser(any(), anyLong())).thenReturn(userDto);
        String response = mvc.perform(patch("/users/{id}", 1L)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(mapper.writeValueAsString(userDto), response);
        verify(service).updateUser(userDto, 1L);
    }

    @Test
    @SneakyThrows
    void updateUserDto_whenEmailIsNotValid_thenDontSaveDataAndThrowException() {
        UserDto userDto = UserDto.builder()
                .email("ee.e").build();
        when(service.updateUser(any(), anyLong())).thenReturn(userDto);
        mvc.perform(patch("/users/{id}", 1L)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
        verifyNoInteractions(service);
    }
}