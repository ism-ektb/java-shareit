package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class ItemForRequestDto {

    private long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}
