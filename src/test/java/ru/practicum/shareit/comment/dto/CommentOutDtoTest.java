package ru.practicum.shareit.comment.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommentOutDtoTest {

    @Test
    void testToString() {
        CommentOutDto commentOutDto = CommentOutDto.builder().build();
        Assertions.assertEquals(commentOutDto.toString(),
                "CommentOutDto(id=null, text=null, authorName=null, created=null)");
    }
}