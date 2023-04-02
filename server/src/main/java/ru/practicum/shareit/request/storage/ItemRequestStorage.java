package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public interface ItemRequestStorage extends JpaRepository<ItemRequest, Long>, ItemRequestCustom {

    @Query("select i from ItemRequest i where i.requestor.id = ?1 order by i.created DESC")
    @EntityGraph(attributePaths = "items")
    List<ItemRequest> findByUserAll(Long id);

    @Query("select i from ItemRequest i where i.requestor.id <> ?1 order by i.created DESC")
    @EntityGraph(attributePaths = "items")
    Page<ItemRequest> findAllForeignPageable(Long id, Pageable pageable);

}
