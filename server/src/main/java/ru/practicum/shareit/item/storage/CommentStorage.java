package ru.practicum.shareit.item.storage;

import org.springframework.data.repository.CrudRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentStorage extends CrudRepository<Comment, Long> {

    List<Comment> findByItem_Id(Long id);

}
