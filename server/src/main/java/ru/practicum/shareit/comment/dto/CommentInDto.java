package ru.practicum.shareit.comment.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class CommentInDto {

  private Long id;
  @NotBlank
  private String text;
}
