package ru.practicum.shareit.user;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

/**
 * POJO класс для описания сущности User
 */
@Entity
@Table(name = "users")
@Getter
@SuperBuilder
@NoArgsConstructor
@ToString
@EqualsAndHashCode(exclude = {"email"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name_user")
    private String name;
    @Column(unique = true)
    @ToString.Exclude
    private String email;
}
