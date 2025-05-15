package ru.alexgur.blog.comment.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.alexgur.blog.system.exception.NotFoundException;
import ru.alexgur.blog.system.exception.InternalServerException;
import ru.alexgur.blog.comment.dto.CommentDto;
import ru.alexgur.blog.comment.interfaces.CommentService;
import ru.alexgur.blog.comment.interfaces.CommentRepository;
import ru.alexgur.blog.comment.mapper.CommentMapper;
import ru.alexgur.blog.comment.model.Comment;
import ru.alexgur.blog.post.interfaces.PostRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional
    public CommentDto add(Long postId, String text) {
        checkPostIdThrowErrorIfNotExist(postId);

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setText(text);

        return commentRepository.add(comment)
                .map(CommentMapper::toDto)
                .orElseThrow(() -> new InternalServerException("Не удалось сохранить комментарий"));
    }

    @Override
    public List<CommentDto> getByPostId(Long postId) {
        checkPostIdThrowErrorIfNotExist(postId);

        return CommentMapper.toDto(commentRepository.getByPostId(postId));
    }

    @Override
    public Map<Long, List<CommentDto>> getByPostId(List<Long> postIds) {
        HashMap<Long, List<CommentDto>> res = new HashMap<>();
        for (Comment comment : commentRepository.getByPostId(postIds)) {
            CommentDto commentDto = CommentMapper.toDto(comment);
            res.computeIfAbsent(comment.getPostId(), k -> new ArrayList<>()).add(commentDto);
        }
        return res;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        commentRepository.delete(id);
    }

    @Override
    @Transactional
    public CommentDto patch(Long commentId, String text) {
        Comment commentSaved = checkAccess(commentId);

        if (!text.isBlank()) {
            commentSaved.setText(text);
        }

        return commentRepository.update(commentSaved)
                .map(CommentMapper::toDto)
                .orElseThrow(() -> new InternalServerException("Не удалось получить комментарий"));
    }

    @Override
    public boolean checkIdExist(Long id) {
        return commentRepository.checkIdExist(id);
    }

    private void checkPostIdThrowErrorIfNotExist(Long postId) {
        if (!postRepository.checkIdExist(postId)) {
            throw new NotFoundException("Публикация с таким id не найдена");
        }
    }

    private Comment checkAccess(Long commentId) {

        if (!checkIdExist(commentId)) {
            throw new NotFoundException("Комментарий с таким id не найден");
        }

        Comment commentSaved = commentRepository.get(commentId).get();

        return commentSaved;
    }
}