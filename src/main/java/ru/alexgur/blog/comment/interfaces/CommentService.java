package ru.alexgur.blog.comment.interfaces;

import java.util.List;
import java.util.Map;

import ru.alexgur.blog.comment.dto.CommentDto;

public interface CommentService {
    CommentDto add(Long postId, String text);

    CommentDto get(Long id);

    List<CommentDto> getByPostId(Long postId);

    Map<Long, List<CommentDto>> getByPostId(List<Long> postIds);

    void delete(Long id);

    CommentDto patch(Long commentId, String text);

    boolean checkIdExist(Long id);
}