package ru.alexgur.blog.comment.interfaces;

import java.util.List;
import java.util.Optional;

import ru.alexgur.blog.comment.model.Comment;

public interface CommentRepository {
    Optional<Comment> add(Comment comment);

    Optional<Comment> get(Long id);

    List<Comment> getByPostId(Long userId);

    Optional<Comment> update(Comment comment);

    void delete(Long id);

    boolean checkIdExist(Long id);

    boolean checkPostExist(Long id);
}