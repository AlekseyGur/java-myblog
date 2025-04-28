package ru.alexgur.blog.comment.interfaces;

import java.util.List;
import java.util.Optional;

import ru.alexgur.blog.comment.model.Comment;

public interface CommentStorage {
    Optional<Comment> add(Comment comment);

    Optional<Comment> get(Long id);

    List<Comment> getByUserId(Long userId);

    Optional<Comment> update(Comment comment);

    void delete(Long id);

    boolean checkIdExist(Long id);

    List<Comment> find(String query);
}