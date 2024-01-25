package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.exception.NoFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapperDto.UserListMapper;
import ru.practicum.shareit.user.mapperDto.UserMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceImplTest {

    @MockBean
    private UserRepository repository;
    @Autowired
    private UserService service;
    @Autowired
    private UserMapper mapper;
    @Autowired
    private UserListMapper listMapper;

    @Test
    void createUser_whenDataIsTrue_thenSaveAndReturnUser() {
        User user = User.builder().id(1L).build();
        UserDto userDto = UserDto.builder().name("Имя").build();
        when(repository.save(any())).thenReturn(user);
        assertEquals(service.createUser(userDto),mapper.modelToDto(user));
        verify(repository).save(mapper.dtoToModel(userDto));
    }

    @Test
    void updateUser_whenDataIsTrue_thenSaveAndReturnUser() {
        UserDto userDto = UserDto.builder().name("Новое Имя").build();
        User user = User.builder().id(1L).name("Имя").build();
        User newUser = User.builder().id(1L).name("Новое Имя").build();
        when(repository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(repository.save(any())).thenReturn(newUser);
        assertEquals(service.updateUser(userDto, 1L), mapper.modelToDto(newUser));
    }

    @Test
    void updateUser_whenUserInDtoIsNotEqUserId_thenThrowExceptionAndDontSave() {
        UserDto userDto = UserDto.builder().name("Новое Имя").id(10L).build();
        final ValidationException e = assertThrows(ValidationException.class, () -> service.updateUser(userDto, 1L));
    }

    @Test
    void findAllUsers_whenDataIsTrue_thenReturnList() {
        User user = User.builder().id(1L).build();
        when(repository.findAll()).thenReturn(List.of(user));
        assertEquals(service.findAllUsers(), listMapper.modelsToDtos(List.of(user)));
    }

    @Test
    void findUserById_whenDataIsTrue_thenReturnUser() {
        User user = User.builder().id(1L).build();
        when(repository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        assertEquals(service.findUserById(1L), mapper.modelToDto(user));
    }

    @Test
    void findUserById_whenUserIsMissing_thenThrowException() {
        User user = User.builder().id(1L).build();
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        final NoFoundException e = assertThrows(NoFoundException.class, () -> service.findUserById(1L));
    }

    @Test
    void findUserByIdForValid_whenUserIsMissing_thenThrowException() {
        User user = User.builder().id(1L).build();
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        final NoFoundException e = assertThrows(NoFoundException.class, () -> service.findUserByIdForValid(1L));
    }

    @Test
    void findUserByIdForValid_whenDataIsTrue_thenReturnUser() {
        User user = User.builder().id(1L).build();
        when(repository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        assertEquals(service.findUserByIdForValid(1L), user);
    }
}