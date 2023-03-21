package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "requests")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotBlank
    private String description;

    @ManyToOne
    @JoinColumn(name = "requestor_id", referencedColumnName = "id", nullable = false)
    private User requestor;

    @Column(name = "created_data")
    @NotNull
    private LocalDateTime created;

    @OneToMany(mappedBy = "request")
    private List<Item> items = new ArrayList<>();
}
