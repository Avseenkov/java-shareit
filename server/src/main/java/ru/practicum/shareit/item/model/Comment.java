package ru.practicum.shareit.item.model;


import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content")
    private String text;

    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id", nullable = false)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id", nullable = false)
    private User author;

    @Column(name = "created_data", nullable = false)
    private LocalDateTime created;
}
