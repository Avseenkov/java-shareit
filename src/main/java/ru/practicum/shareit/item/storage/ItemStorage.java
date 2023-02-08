package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage extends JpaRepository<Item, Long> {
    List<Item> findByOwner_Id(Long id);

    List<Item> findAllByOwner_IdOrderById(Long id);

    @Query("select i from Item i " +
            "where i.available = true " +
            "and (upper(i.name) like upper(concat('%', ?1, '%')) " +
            "or upper(i.description) like upper(concat('%', ?1, '%')))")
    List<Item> findItems(String query);

    Optional<Item> findByIdAndAvailableTrue(Long id);


}
