package ru.practicum.shareit.user.mapperDto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserWithoutEmailDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User dtoToModel(UserDto userDto);

    UserDto modelToDto(User user);

    UserWithoutEmailDto modelToDtoWithoutEmail(User user);

    User dtoWithoutEmailToModel(UserWithoutEmailDto userWithoutEmailDto);
/*
    @Mapping(target = "userId", source = "user.getId()")
    Long modelToIdDto(User user);
    @Mapping(target = "user.id", source = "userId")
    User idDtoToModel(Long userId);


 */
}
