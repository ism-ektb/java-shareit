package ru.practicum.shareit.user.mapperDto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface UserListMapper {
    List<User> dtosToModels(List<UserDto> dtos);

    List<UserDto> modelsToDtos(List<User> users);
}
