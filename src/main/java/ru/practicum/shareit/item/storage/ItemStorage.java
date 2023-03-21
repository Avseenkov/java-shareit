package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage extends JpaRepository<Item, Long>, ItemStorageCustom {

    List<Item> findAllByOwner_IdOrderById(Long id, Pageable page);

    @Query("select i from Item i " +
            "where i.available = true " +
            "and (upper(i.name) like upper(concat('%', ?1, '%')) " +
            "or upper(i.description) like upper(concat('%', ?1, '%')))")
    List<Item> findItems(String query, Pageable page);

    @Query("select i from Item i where i.id = ?1")
    @EntityGraph(attributePaths = "comments")
    Optional<Item> findByIdWithComments(Long id);

}
