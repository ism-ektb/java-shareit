package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.dto.SimpleBookingDto;
import ru.practicum.shareit.booking.dtoMapper.BookingMapper;
import ru.practicum.shareit.exception.NoFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.BookingStatus.*;

@SpringBootTest
@Slf4j
class BookingServiceImplTest {
    @Autowired
    private BookingService service;
    @Autowired
    private BookingMapper mapper;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private BookingRepository repository;

    @MockBean
    private UserService userService;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(4L).build();
    }

    @Test
    void create_whenItemIsMissing_thenIsNotCallingRepositoryAndThrowException() {
        SimpleBookingDto simpleBookingDto = SimpleBookingDto.builder()
                .itemId(3L)
                .build();
        when(itemRepository.findById(any())).thenReturn(Optional.empty());
        final NoFoundException e = assertThrows(NoFoundException.class, () ->
                service.create(10L, simpleBookingDto));
        verifyNoInteractions(repository);
    }

    @Test
    void create_whenUserIdIsNotValid_thenIsNotCallingRepositoryAndThrowException() {
        SimpleBookingDto simpleBookingDto = SimpleBookingDto.builder()
                .itemId(3L)
                .build();
        Item item = Item.builder().id(3L).build();
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));
        when(userService.findUserByIdForValid(anyLong())).thenThrow(new NoFoundException("Юзер не найден"));
        final NoFoundException e = assertThrows(NoFoundException.class, () ->
                service.create(10L, simpleBookingDto));
        verifyNoInteractions(repository);
    }

    @Test
    void create_whenItemIdIsNotAvailable_thenIsNotCallingRepositoryAndThrowException() {
        SimpleBookingDto simpleBookingDto = SimpleBookingDto.builder()
                .itemId(3L)
                .build();
        Item item = Item.builder().id(3L).available(false).build();
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));
        when(userService.findUserByIdForValid(anyLong())).thenReturn(user);
        final ValidationException e = assertThrows(ValidationException.class, () ->
                service.create(3L, simpleBookingDto));
        verifyNoInteractions(repository);
    }

    @Test
    void create_whenBookerEqualOwner_thenIsNotCallingRepositoryAndThrowException() {
        SimpleBookingDto simpleBookingDto = SimpleBookingDto.builder()
                .itemId(3L)
                .build();
        Item item = Item.builder().id(3L).available(true).owner(user).build();
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));
        when(userService.findUserByIdForValid(anyLong())).thenReturn(user);
        final NoFoundException e = assertThrows(NoFoundException.class, () ->
                service.create(3L, simpleBookingDto));
        verifyNoInteractions(repository);
    }

    @Test
    void create_whenDataIsValid_thenIsCallingRepositoryStatusEqWaitingAndReturnBooking() {
        SimpleBookingDto simpleBookingDto = SimpleBookingDto.builder()
                .itemId(3L)
                .build();
        Item item = Item.builder().id(3L).available(true).owner(User.builder().id(1L).build()).build();
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));
        when(userService.findUserByIdForValid(anyLong())).thenReturn(user);
        Booking booking = Booking.builder().item(item).booker(user).status(WAITING).build();
        when(repository.save(any())).thenReturn(booking);
        assertEquals(service.create(3L, simpleBookingDto), mapper.modelToDto(booking));
        verify(repository).save(booking);
    }

    @Test
    void getById_whenBookingIsMissing_thenThrowException() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        final NoFoundException e = assertThrows(NoFoundException.class, () ->
                service.getById(3L, 4L));
        verify(repository).findById(4L);
    }

    @Test
    void getById_whenUserIsMissing_thenThrowException() {
        Booking booking = Booking.builder().id(4L).build();
        when(repository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(userService.findUserByIdForValid(anyLong())).thenThrow(new NoFoundException("юзер не найден"));
        final NoFoundException e = assertThrows(NoFoundException.class, () ->
                service.getById(3L, 4L));
        verify(repository).findById(4L);
    }

    @Test
    void getById_whenUserIsNotEqOwnerAndIsNotEqBooker_thenThrowException() {
        user = User.builder().id(10L).build();
        Booking booking = Booking.builder()
                .id(4L)
                .booker(User.builder().id(4L).build())
                .item(Item.builder()
                        .owner(User.builder()
                                .id(1L).build()).build()).build();
        when(repository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(userService.findUserByIdForValid(anyLong())).thenReturn(user);
        final NoFoundException e = assertThrows(NoFoundException.class, () ->
                service.getById(3L, 4L));
        verify(repository).findById(4L);

    }

    @Test
    void getById_whenUserIsOwner_thenReturnBooking() {
        Booking booking = Booking.builder()
                .id(4L)
                .booker(User.builder().id(4L).build())
                .item(Item.builder()
                        .owner(user).build()).build();
        when(repository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(userService.findUserByIdForValid(anyLong())).thenReturn(user);
        assertEquals(service.getById(3L, 4L), mapper.modelToDto(booking));
    }

    @Test
    void approved_whenBookingIsMissing_thenNoMoreCallRepositoryAndThrowException() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        final NoFoundException e = assertThrows(NoFoundException.class, () ->
                service.approved(3L, 4L, true));
        verify(repository).findById(4L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void approved_whenUserIsMissing_thenNoMoreCallRepositoryAndThrowException() {
        Booking booking = Booking.builder().id(4L).build();
        when(repository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(userService.findUserByIdForValid(anyLong())).thenThrow(new NoFoundException("юзер не найден"));
        final NoFoundException e = assertThrows(NoFoundException.class, () ->
                service.approved(3L, 4L, true));
        verify(repository).findById(4L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void approved_whenUserIsNotEqOwner_thenNoMoreCallRepositoryAndThrowException() {
        Booking booking = Booking.builder()
                .id(4L)
                .item(Item.builder()
                        .owner(User.builder()
                                .id(1L).build()).build()).build();
        when(repository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(userService.findUserByIdForValid(anyLong())).thenReturn(user);
        final NoFoundException e = assertThrows(NoFoundException.class, () ->
                service.approved(3L, 4L, true));
        verify(repository).findById(4L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void approved_whenStatusOfBookingIsApproved_thenNoMoreCallRepositoryAndThrowException() {
        Booking booking = Booking.builder()
                .id(4L)
                .item(Item.builder()
                        .owner(user).build())
                .status(APPROVED).build();
        when(repository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(userService.findUserByIdForValid(anyLong())).thenReturn(user);
        final ValidationException e = assertThrows(ValidationException.class, () ->
                service.approved(3L, 4L, true));
        verify(repository).findById(4L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void approved_whenStatusOfBookingIsRejected_thenNoMoreCallRepositoryAndThrowException() {
        Booking booking = Booking.builder()
                .id(4L)
                .item(Item.builder()
                        .owner(user).build())
                .status(REJECTED).build();
        when(repository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(userService.findUserByIdForValid(anyLong())).thenReturn(user);
        final ValidationException e = assertThrows(ValidationException.class, () ->
                service.approved(3L, 4L, true));
        verify(repository).findById(4L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void approved_whenDataIsValidAndApprovedTrue_thenCallAndSaveRepository() {
        Booking booking = Booking.builder()
                .id(4L)
                .item(Item.builder()
                        .owner(user).build())
                .status(WAITING).build();
        Booking testBooking = Booking.builder()
                .id(4L)
                .item(Item.builder()
                        .owner(user).build())
                .status(APPROVED).build();
        log.info("booking: {} testBooking {}", booking, testBooking);
        when(repository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(userService.findUserByIdForValid(anyLong())).thenReturn(user);
        when(repository.save(any())).thenReturn(testBooking);
        assertEquals(service.approved(3L, 4L, true), mapper.modelToDto(testBooking));
        verify(repository).save(testBooking);

    }
}