package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findItemsByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrueOrderById(
            String textName, String textDescription, Pageable pageRequest);

    List<Item> findItemsByOwnerEqualsOrderById(User owner, Pageable pageRequest);

    List<Item> findAllByRequestId(Long requestId);
}
