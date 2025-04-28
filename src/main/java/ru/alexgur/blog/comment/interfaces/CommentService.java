package ru.alexgur.blog.comment.interfaces;

import java.util.List;

import ru.alexgur.blog.comment.dto.CommentDto;

public interface CommentService {
    CommentDto add(CommentDto commentDto);

    CommentDto get(Long id);

    List<CommentDto> getByPostId(Long postId);

    void delete(Long id);

    CommentDto patch(CommentDto comment);

    boolean checkIdExist(Long id);
}