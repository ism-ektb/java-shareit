package ru.practicum.shareit.comment.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class CommentOutDto {
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
