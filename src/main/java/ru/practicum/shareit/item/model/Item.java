package ru.practicum.shareit.item.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

/**
 * POJO Item
 */
@Entity
@Table(name = "items",
        uniqueConstraints =
                {@UniqueConstraint(name = "NameAndOwner",
                        columnNames = {"name", "owner_id"})})
@SuperBuilder
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String description;
    private Boolean available;
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    @ManyToOne
    private User owner;
    @ManyToOne
    @JoinColumn(name = "request_id", referencedColumnName = "id")
    private ItemRequest request;
}
