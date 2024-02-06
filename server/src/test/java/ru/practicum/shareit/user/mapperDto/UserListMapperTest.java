package ru.practicum.shareit.user.mapperDto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserListMapperTest {

    @Autowired
    private UserListMapper mapper;

    @Test
    void dtosToModels() {
        User user = User.builder().id(1L).build();
        UserDto userDto = UserDto.builder().id(1L).build();
        assertEquals(mapper.modelsToDtos(List.of(user)), List.of(userDto));
    }

    @Test
    void modelsToDtos() {
        User user = User.builder().id(1L).build();
        UserDto userDto = UserDto.builder().id(1L).build();
        assertEquals(mapper.dtosToModels(List.of(userDto)), List.of(user));
    }
}