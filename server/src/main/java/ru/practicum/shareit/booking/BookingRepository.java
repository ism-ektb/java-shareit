package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId, Pageable pageRequest);

    List<Booking>
    findAllByBookerIdAndAndStatusEqualsOrderByStartDesc(Long bookerId, BookingStatus status, Pageable pageRequest);

    List<Booking>
    findAllByBookerIdAndAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now, Pageable pageRequest);

    List<Booking>
    findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now, Pageable pageRequest);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long bookerId, LocalDateTime now, LocalDateTime thenNow, Pageable pageRequest);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId, Pageable pageRequest);

    List<Booking>
    findAllByItemOwnerIdAndAndStatusEqualsOrderByStartDesc(Long ownerId, BookingStatus status, Pageable pageRequest);

    List<Booking>
    findAllByItemOwnerIdAndAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime now, Pageable pageRequest);

    List<Booking>
    findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime now, Pageable pageRequest);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long ownerId, LocalDateTime now, LocalDateTime thenNow, Pageable pageRequest);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.item.id = ?1 AND b.start < ?2 " +
            "AND b.status IN ( ?3, ?4 ) " +
            "ORDER BY b.end DESC")
    List<Booking> findAllLast(
            Long itemId, LocalDateTime now, BookingStatus status1, BookingStatus status2);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.item.id = ?1 AND b.start > ?2 " +
            "AND b.status IN ( ?3, ?4 ) " +
            "ORDER BY b.end")
    List<Booking> findAllNext(
            Long itemId, LocalDateTime now, BookingStatus status1, BookingStatus status2);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.booker.id = ?1 AND b.item.id = ?2 AND b.end < ?3")

    List<Booking> findAllFinishByBookerIdByItemId(
            Long bookerId, Long itemId, LocalDateTime now);
}
