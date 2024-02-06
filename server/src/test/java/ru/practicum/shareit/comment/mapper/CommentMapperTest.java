package ru.practicum.shareit.comment.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.dto.CommentOutDto;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentMapperTest {

    @Autowired
    private CommentMapper mapper;

    @Test
    void modelToOutDto() {
        Comment comment = Comment.builder().author(User.builder().name("имя").build()).build();
        CommentOutDto commentOutDto = CommentOutDto.builder().authorName("имя").build();
        assertEquals(mapper.modelToOutDto(comment), commentOutDto);
    }
}