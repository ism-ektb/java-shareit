package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.SimpleBookingDto;
import ru.practicum.shareit.booking.dtoMapper.BookingListMapper;
import ru.practicum.shareit.booking.dtoMapper.BookingMapper;
import ru.practicum.shareit.exception.NoFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.BookingStatus.*;

@SpringBootTest
class BookingServiceImplTest {
    @Autowired
    private BookingService service;
    @Autowired
    private BookingMapper mapper;
    @Autowired
    private BookingListMapper listMapper;
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
        when(repository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(userService.findUserByIdForValid(anyLong())).thenReturn(user);
        when(repository.save(any())).thenReturn(testBooking);
        assertEquals(service.approved(3L, 4L, true), mapper.modelToDto(testBooking));
        verify(repository).save(testBooking);
    }

    @Test
    void findAllForBooker_whenTrueDate_thenReturnList() {
        when(userService.findUserByIdForValid(anyLong())).thenReturn(user);
        List<Booking> list = List.of(Booking.builder().id(1L).build());
        when(repository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any()))
                .thenReturn(list);
        assertEquals(service.findAllForBooker(2L, CURRENT, PageRequest.of(0, 10)), listMapper.modelsToDtos(list));
    }

    @Test
    void findAllForOwner_whenTrueDate_thenReturnList() {
        when(userService.findUserByIdForValid(anyLong())).thenReturn(user);
        List<Booking> list = List.of(Booking.builder().id(1L).build());
        when(repository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any()))
                .thenReturn(list);
        assertEquals(service.findAllForOwner(2L, CURRENT, PageRequest.of(0, 10)), listMapper.modelsToDtos(list));
    }

    @Test
    void findAllForOwner_whenSendPast_thenReturnList() {
        when(userService.findUserByIdForValid(anyLong())).thenReturn(user);
        List<Booking> list = List.of(Booking.builder().id(1L).build());
        when(repository.findAllByItemOwnerIdAndAndEndBeforeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(list);
        assertEquals(service.findAllForOwner(2L, PAST, PageRequest.of(0, 10)), listMapper.modelsToDtos(list));
    }

    @Test
    void findAllForOwner_whenSendFuture_thenReturnList() {
        when(userService.findUserByIdForValid(anyLong())).thenReturn(user);
        List<Booking> list = List.of(Booking.builder().id(1L).build());
        when(repository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(list);
        assertEquals(service.findAllForOwner(2L, FUTURE, PageRequest.of(0, 10)), listMapper.modelsToDtos(list));
    }

    @Test
    void findAllForOwner_whenSendWaiting_thenReturnList() {
        when(userService.findUserByIdForValid(anyLong())).thenReturn(user);
        List<Booking> list = List.of(Booking.builder().id(1L).build());
        when(repository.findAllByItemOwnerIdAndAndStatusEqualsOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(list);
        assertEquals(service.findAllForOwner(2L, WAITING, PageRequest.of(0, 10)), listMapper.modelsToDtos(list));
    }

    @Test
    void findAllForOwner_whenSendAll_thenReturnList() {
        when(userService.findUserByIdForValid(anyLong())).thenReturn(user);
        List<Booking> list = List.of(Booking.builder().id(1L).build());
        when(repository.findAllByItemOwnerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(list);
        assertEquals(service.findAllForOwner(2L, ALL, PageRequest.of(0, 10)), listMapper.modelsToDtos(list));
    }

    @Test
    void findAllForBooker_whenSendAll_thenReturnList() {
        when(userService.findUserByIdForValid(anyLong())).thenReturn(user);
        List<Booking> list = List.of(Booking.builder().id(1L).build());
        when(repository.findAllByBookerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(list);
        assertEquals(service.findAllForBooker(2L, ALL, PageRequest.of(0, 10)), listMapper.modelsToDtos(list));
    }

    @Test
    void findAllForBooker_whenSendWaiting_thenReturnList() {
        when(userService.findUserByIdForValid(anyLong())).thenReturn(user);
        List<Booking> list = List.of(Booking.builder().id(1L).build());
        when(repository.findAllByBookerIdAndAndStatusEqualsOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(list);
        assertEquals(service.findAllForBooker(2L, WAITING, PageRequest.of(0, 10)), listMapper.modelsToDtos(list));
    }

    @Test
    void findAllForBooker_whenSendFuture_thenReturnList() {
        when(userService.findUserByIdForValid(anyLong())).thenReturn(user);
        List<Booking> list = List.of(Booking.builder().id(1L).build());
        when(repository.findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(list);
        assertEquals(service.findAllForBooker(2L, FUTURE, PageRequest.of(0, 10)), listMapper.modelsToDtos(list));
    }

    @Test
    void findAllForBooker_whenSendPast_thenReturnList() {
        when(userService.findUserByIdForValid(anyLong())).thenReturn(user);
        List<Booking> list = List.of(Booking.builder().id(1L).build());
        when(repository.findAllByBookerIdAndAndEndBeforeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(list);
        assertEquals(service.findAllForBooker(2L, PAST, PageRequest.of(0, 10)), listMapper.modelsToDtos(list));
    }

    @Test
    void findAllForBooker_whenSendCurrent_thenReturnList() {
        when(userService.findUserByIdForValid(anyLong())).thenReturn(user);
        List<Booking> list = List.of(Booking.builder().id(1L).build());
        when(repository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any()))
                .thenReturn(list);
        assertEquals(service.findAllForBooker(2L, CURRENT, PageRequest.of(0, 10)), listMapper.modelsToDtos(list));
    }

    @Test
    void getLastByItem_whenTrueDate_thenReturnBooking() {
        List<Booking> list = List.of(Booking.builder().id(1L).build());
        when(repository.findAllLast(anyLong(), any(), any(), any()))
                .thenReturn(list);
        assertEquals(service.getLastByItem(2L), mapper.modelToDtoForItem(list.get(0)));
    }

    @Test
    void getNextByItem_whenTrueDate_thenReturnBooking() {
        List<Booking> list = List.of(Booking.builder().id(1L).build());
        when(repository.findAllNext(anyLong(), any(), any(), any()))
                .thenReturn(list);
        assertEquals(service.getNextByItem(2L), mapper.modelToDtoForItem(list.get(0)));
    }

    @Test
    void findAllFinishByItemByUser_whenDataTrue_thenReturnList() {
        List<Booking> list = List.of(Booking.builder().id(1L).build());
        when(repository.findAllFinishByBookerIdByItemId(anyLong(), anyLong(), any())).thenReturn(list);
        assertEquals(service.findAllFinishByItemByUser(1L, 2L), list);
    }
}