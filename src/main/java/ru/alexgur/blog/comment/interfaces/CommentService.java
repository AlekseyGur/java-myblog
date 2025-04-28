package ru.alexgur.blog.comment.interfaces;

import java.util.List;

import ru.alexgur.blog.comment.dto.CommentDto;

public interface CommentService {
    CommentDto add(CommentDto comment);

    CommentDto get(Long id);

    List<CommentDto> find(String query);

    List<CommentDto> getByUserId(Long userId);

    void delete(Long id);

    CommentDto patch(CommentDto comment);

    boolean checkIdExist(Long id);
}