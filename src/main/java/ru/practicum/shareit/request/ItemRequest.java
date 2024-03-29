package ru.practicum.shareit.request;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * POJO item-requests.
 */
@Entity
@Table(name = "item_requests")
@Getter
@SuperBuilder
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String description;
    @ManyToOne
    @JoinColumn(name = "requestor_id", referencedColumnName = "id")
    private User requestor;
    private LocalDateTime created;

}
