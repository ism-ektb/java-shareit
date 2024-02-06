package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentInDto;
import ru.practicum.shareit.comment.mapper.CommentListMapper;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.exception.NoFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentDto;
import ru.practicum.shareit.item.dtoMapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
class ItemServiceImplTest {

    @Autowired
    private ItemService service;
    @MockBean
    private ItemRepository repository;
    @MockBean
    private UserService userService;
    @MockBean
    private BookingService bookingService;
    @MockBean
    private CommentRepository commentRepository;
    @Autowired
    private ItemMapper mapper;
    @Autowired
    private CommentListMapper commentListMapper;
    @Autowired
    private CommentMapper commentMapper;

    ItemDto itemDto;
    long userId;
    User user;
    Item item;
    CommentInDto commentInDto;
    Comment comment;

    @BeforeEach
    void setUp() {
        itemDto = ItemDto.builder().name("Описание").build();
        item = mapper.dtoToModel(itemDto);
        userId = 1L;
        user = User.builder().id(userId).name("имя").build();
        commentInDto = CommentInDto.builder().text("коммент").build();
        comment = Comment.builder()
                .text("коммент")
                .item(Item.builder().name("Описание").owner(user).build())
                .build();
    }

    @Test
    void createItem_whenParamIsValid_thenSaveAndReturnItem() {
        item.setOwner(user);
        when(userService.findUserByIdForValid(anyLong())).thenReturn(user);
        when(repository.save(any())).thenReturn(item);
        assertEquals(service.createItem(itemDto, userId), mapper.modelToDto(item));
        verify(repository).save(item);
    }

    @Test
    void createItem_whenParamIsNotValid_thenDontSaveAndReturnItem() {
        item.setOwner(user);
        when(userService.findUserByIdForValid(anyLong())).thenThrow(new NoFoundException("Ничего не найдено"));
        when(repository.save(any())).thenReturn(item);
        final NoFoundException e = assertThrows(NoFoundException.class, () -> service.createItem(itemDto, userId));
        verifyNoInteractions(repository);
    }

    @Test
    void updateItem_whenSendTrueParam_thenItemIsSaveAndReturnItem() {
        item.setOwner(user);
        item.setAvailable(true);
        when(userService.findUserByIdForValid(anyLong())).thenReturn(user);
        when(repository.save(any())).thenReturn(item);
        when(repository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        assertEquals(service.updateItem(userId, 3L, itemDto), mapper.modelToDto(item));
    }

    @Test
    void updateItem_whenSendFailItemId_thenItemIsNotSaveAndReturnException() {
        item.setOwner(user);
        item.setAvailable(true);
        when(userService.findUserByIdForValid(anyLong())).thenReturn(user);
        when(repository.save(any())).thenReturn(item);
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        final NoFoundException e = assertThrows(NoFoundException.class, () -> service.updateItem(userId, 3L, itemDto));
        verify(repository).findById(3L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void updateItem_whenSendFailUserIdWhichIsNotOwnerItem_thenItemIsNotSaveAndReturnException() {
        item.setOwner(user);
        item.setAvailable(true);
        when(userService.findUserByIdForValid(anyLong())).thenReturn(user);
        when(repository.save(any())).thenReturn(item);
        when(repository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        final ValidationException e = assertThrows(ValidationException.class, () -> service.updateItem(10L, 3L, itemDto));
        verify(repository).findById(3L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getItemOfId_whenSendTrueParam_thenReturnItemWithBooking() {
        item.setOwner(user);
        BookingForItemDto bookingL = BookingForItemDto.builder().id(1L).build();
        BookingForItemDto bookingN = BookingForItemDto.builder().id(2L).build();
        when(bookingService.getLastByItem(anyLong())).thenReturn(bookingL);
        when(bookingService.getNextByItem(anyLong())).thenReturn(bookingN);
        when(repository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(commentRepository.findCommentsByItemId(anyLong())).thenReturn(null);
        ItemWithBookingAndCommentDto itemDto = mapper.modelToDtoWithBooking(item);
        itemDto.setLastBooking(bookingL);
        itemDto.setNextBooking(bookingN);
        assertEquals(service.getItemOfId(userId, 3L), itemDto);
    }

    @Test
    void getItemOfId_whenUserIdIsNotEqualOwnerId_thenReturnItemWithoutBooking() {
        item.setOwner(user);
        BookingForItemDto bookingL = BookingForItemDto.builder().id(1L).build();
        BookingForItemDto bookingN = BookingForItemDto.builder().id(2L).build();
        when(bookingService.getLastByItem(anyLong())).thenReturn(bookingL);
        when(bookingService.getNextByItem(anyLong())).thenReturn(bookingN);
        when(repository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(commentRepository.findCommentsByItemId(anyLong())).thenReturn(null);
        ItemWithBookingAndCommentDto itemDto = mapper.modelToDtoWithBooking(item);
        assertEquals(service.getItemOfId(10L, 3L), itemDto);
    }

    @Test
    void getItemOfId_whenItemIdIsFail_thenReturnException() {
        item.setOwner(user);
        BookingForItemDto bookingL = BookingForItemDto.builder().id(1L).build();
        BookingForItemDto bookingN = BookingForItemDto.builder().id(2L).build();
        when(bookingService.getLastByItem(anyLong())).thenReturn(bookingL);
        when(bookingService.getNextByItem(anyLong())).thenReturn(bookingN);
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        when(commentRepository.findCommentsByItemId(anyLong())).thenReturn(null);
        ItemWithBookingAndCommentDto itemDto = mapper.modelToDtoWithBooking(item);
        final NoFoundException e = assertThrows(NoFoundException.class, () -> service.getItemOfId(10L, 3L));
    }

    @Test
    void getItems_whenUserIdIsFail_thenThrowNoFound() {
        when(userService.findUserByIdForValid(anyLong())).thenThrow(new NoFoundException("Юзер не найден"));
        final NoFoundException e = assertThrows(NoFoundException.class, () ->
                service.getItems(userId, PageRequest.of(0, 10)));
        verifyNoInteractions(repository);
    }

    @Test
    void getItems_whenDataIsTrue_thenReturnList() {
        when(userService.findUserByIdForValid(anyLong())).thenReturn(user);
        BookingForItemDto bookingL = BookingForItemDto.builder().id(1L).build();
        BookingForItemDto bookingN = BookingForItemDto.builder().id(2L).build();
        when(bookingService.getLastByItem(anyLong())).thenReturn(bookingL);
        when(bookingService.getNextByItem(anyLong())).thenReturn(bookingN);
        when(repository.findItemsByOwnerEqualsOrderById(any(), any())).thenReturn(List.of(item));
        when(commentRepository.findCommentsByItemId(anyLong())).thenReturn(null);
        ItemWithBookingAndCommentDto itemDto = mapper.modelToDtoWithBooking(item);
        itemDto.setLastBooking(bookingL);
        itemDto.setNextBooking(bookingN);

        assertEquals(service.getItems(userId, PageRequest.of(0, 10)), List.of(itemDto));
    }

    @Test
    void getItemOfText_whenTextIsBlank_thenReturnEmptyList() {
        assertEquals(service.getItemOfText(userId, "   ", PageRequest.of(0, 10)), new ArrayList<>());
        verifyNoInteractions(repository);
    }

    @Test
    void getItemOfText_whenUserIdIsNotValid_thenThrowException() {
        when(userService.findUserByIdForValid(anyLong())).thenThrow(new NoFoundException("Юзер не найден"));
        final NoFoundException e = assertThrows(NoFoundException.class, () ->
                service.getItemOfText(userId, "текст", PageRequest.of(0, 10)));
        verifyNoInteractions(repository);
    }

    @Test
    void getItemOfText_whenDataIsValid_thenReturnList() {
        when(userService.findUserByIdForValid(anyLong())).thenReturn(user);
        when(repository.findItemsByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrueOrderById(
                anyString(), anyString(), any())).thenReturn(List.of(item));
        assertEquals(service.getItemOfText(userId, "текст", PageRequest.of(0, 10)), List.of(itemDto));
    }

    @Test
    void addComment_whenUserIdIsNotValid_thenThrowException() {
        when(userService.findUserByIdForValid(anyLong())).thenThrow(new NoFoundException("Юзер не найден"));
        final NoFoundException e = assertThrows(NoFoundException.class, () ->
                service.addComment(userId, 3L, commentInDto));
        verifyNoInteractions(repository);
    }

    @Test
    void addComment_whenItemIdIsNotValid_thenThrowException() {
        when(userService.findUserByIdForValid(anyLong())).thenReturn(user);
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        final NoFoundException e = assertThrows(NoFoundException.class, () ->
                service.addComment(userId, 3L, commentInDto));
    }

    @Test
    void addComment_whenParamsIsValid_thenSaveAndReturnComment() {
        when(userService.findUserByIdForValid(anyLong())).thenReturn(user);
        when(repository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(bookingService.findAllFinishByItemByUser(anyLong(), anyLong()))
                .thenReturn(List.of(Booking.builder().build()));
        when(commentRepository.save(any())).thenReturn(comment);
        assertEquals(service.addComment(userId, 3L, commentInDto), commentMapper.modelToOutDto(comment));
    }
}