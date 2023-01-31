package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.booking.validation.OnCreate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.Future;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Future(groups = OnCreate.class)
    @Column(name = "start_date")
    LocalDateTime start;

    @Future(groups = OnCreate.class)
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



