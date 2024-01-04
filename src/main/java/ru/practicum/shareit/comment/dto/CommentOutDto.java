package ru.practicum.shareit.comment.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentOutDto {
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
