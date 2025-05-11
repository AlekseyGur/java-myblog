package ru.alexgur.blog.comment.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.alexgur.blog.system.exception.NotFoundException;
import ru.alexgur.blog.comment.dto.CommentDto;
import ru.alexgur.blog.comment.interfaces.CommentService;
import ru.alexgur.blog.comment.interfaces.CommentRepository;
import ru.alexgur.blog.comment.mapper.CommentMapper;
import ru.alexgur.blog.comment.model.Comment;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentStorage;

    @Override
    public CommentDto add(Long postId, String text) {
        checkPostExist(postId);

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setText(text);
        return CommentMapper.toDto(commentStorage.add(comment).orElse(null));
    }

    @Override
    public CommentDto get(Long postId) {
        return CommentMapper.toDto(commentStorage.get(postId).orElse(null));
    }

    @Override
    public List<CommentDto> getByPostId(Long postId) {
        return CommentMapper.toDto(commentStorage.getByPostId(postId));
    }

    @Override
    public Map<Long, List<CommentDto>> getByPostId(List<Long> postIds) {
        HashMap<Long, List<CommentDto>> res = new HashMap<>();
        for (Comment comment : commentStorage.getByPostId(postIds)) {
            CommentDto commentDto = CommentMapper.toDto(comment);
            res.computeIfAbsent(comment.getPostId(), k -> new ArrayList<>()).add(commentDto);
        }
        return res;
    }

    @Override
    public void delete(Long id) {
        commentStorage.delete(id);
    }

    @Override
    public CommentDto patch(Long commentId, String text) {
        Comment commentSaved = checkAccess(commentId);

        System.out.println(text);
        if (text != null) {
            commentSaved.setText(text);
        }

        return CommentMapper.toDto(commentStorage.update(commentSaved).orElse(null));
    }

    @Override
    public boolean checkIdExist(Long id) {
        return commentStorage.checkIdExist(id);
    }

    private Comment checkAccess(Long commentId) {

        if (commentId == null || !checkIdExist(commentId)) {
            throw new NotFoundException("Комментарий с таким id не найден");
        }

        Comment commentSaved = commentStorage.get(commentId).get();

        return commentSaved;
    }

    private void checkPostExist(Long postId) {
        if (!commentStorage.checkPostExist(postId)) {
            throw new NotFoundException("Публикация с таким id не найдена");
        }
    }
}