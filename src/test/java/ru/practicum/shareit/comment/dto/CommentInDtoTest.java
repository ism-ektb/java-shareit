package ru.practicum.shareit.comment.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommentInDtoTest {

    @Test
    void testToString() {
        CommentInDto commentInDto = CommentInDto.builder().build();
        Assertions.assertEquals(commentInDto.toString(), "CommentInDto(id=null, text=null)");
    }
}