package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "start_date")
    LocalDateTime start;

    @Column(name = "end_date")
    LocalDateTime end;

    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id", nullable = false)
    Item item;

    @ManyToOne
    @JoinColumn(name = "booker_id", referencedColumnName = "id", nullable = false)
    User booker;

    @Enumerated(EnumType.STRING)
    Status status;

}



