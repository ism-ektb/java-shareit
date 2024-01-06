package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.comment.dto.CommentOutDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserWithoutEmailDto;

import java.util.List;

@Data
@Builder
public class ItemWithBookingAndCommentDto {

    private long id;
    private String name;
    private String description;
    private Boolean available;
    private UserWithoutEmailDto owner;
    private ItemRequestDto request;
    private BookingForItemDto lastBooking;
    private BookingForItemDto nextBooking;
    private List<CommentOutDto> comments;
}
