package ru.practicum.shareit.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.dto.CommentInDto;
import ru.practicum.shareit.comment.dto.CommentOutDto;
import ru.practicum.shareit.item.dtoMapper.ItemMapper;
import ru.practicum.shareit.user.mapperDto.UserMapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class})
public interface CommentMapper {
    @Mapping(target = "authorName", source = "comment.author.name")
    CommentOutDto modelToOutDto(Comment comment);

    Comment dtoInToModel(CommentInDto comment);
}
