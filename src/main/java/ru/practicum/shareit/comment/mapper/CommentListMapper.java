package ru.practicum.shareit.comment.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.dto.CommentOutDto;

import java.util.List;

@Mapper(componentModel = "spring", uses = CommentMapper.class)
public interface CommentListMapper {
    List<CommentOutDto> ModelsToInDtos(List<Comment> comments);
}
