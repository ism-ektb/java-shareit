package ru.practicum.shareit.user.mapperDto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserWithoutEmailDto;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper mapper;

    @Test
    void dtoToModel() {
        User user = User.builder().id(1L).build();
        UserDto userDto = UserDto.builder().id(1L).build();
        assertEquals(mapper.modelToDto(user), userDto);
    }

    @Test
    void modelToDto() {
        User user = User.builder().id(1L).build();
        UserDto userDto = UserDto.builder().id(1L).build();
        assertEquals(mapper.dtoToModel(userDto), user);
    }

    @Test
    void modelToDtoWithoutEmail() {
        User user = User.builder().id(1L).build();
        UserWithoutEmailDto userDto = UserWithoutEmailDto.builder().id(1L).build();
        assertEquals(mapper.modelToDtoWithoutEmail(user), userDto);
    }

    @Test
    void dtoWithoutEmailToModel() {
        User user = User.builder().id(1L).build();
        UserWithoutEmailDto userDto = UserWithoutEmailDto.builder().id(1L).build();
        assertEquals(mapper.dtoWithoutEmailToModel(userDto), user);
    }
}