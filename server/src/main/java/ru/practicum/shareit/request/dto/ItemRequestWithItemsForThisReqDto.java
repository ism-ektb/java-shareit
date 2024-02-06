package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class ItemRequestWithItemsForThisReqDto {
    private long id;
    private String description;
    private UserDto requestor;
    private LocalDateTime created;
    private List<ItemForRequestDto> items;
}
