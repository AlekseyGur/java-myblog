package ru.alexgur.blog.comment.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        Comment comment = new Comment(null, postId, text);

        checkPostExist(comment.getPostId());

        return CommentMapper.commentToDto(commentStorage.add(comment).orElse(null));
    }

    @Override
    public CommentDto get(Long postId) {
        return CommentMapper.commentToDto(commentStorage.get(postId).orElse(null));
    }

    @Override
    public List<CommentDto> getByPostId(Long postId) {
        return CommentMapper.commentToDto(commentStorage.getByPostId(postId));
    }

    @Override
    public Map<Long, List<CommentDto>> getByPostId(List<Long> postIds) {

        return commentStorage.getByPostId(postIds)
                .stream()
                .map(CommentMapper::commentToDto)
                .collect(Collectors.groupingBy(CommentDto::getPostId, Collectors.toList()));
    }

    @Override
    public void delete(Long id) {
        commentStorage.delete(id);
    }

    @Override
    public CommentDto patch(CommentDto commentDto) {
        Comment commentSaved = checkAccess(commentDto);

        if (commentDto.getText() != null) {
            commentSaved.setText(commentDto.getText());
        }

        return CommentMapper.commentToDto(commentStorage.update(commentSaved).orElse(null));
    }

    @Override
    public boolean checkIdExist(Long id) {
        return commentStorage.checkIdExist(id);
    }

    private Comment checkAccess(CommentDto comment) {
        Long commentId = comment.getId();

        if (comment == null || !checkIdExist(commentId)) {
            throw new NotFoundException("Комментарий с таким id не найден");
        }

        checkPostExist(comment.getPostId());

        Comment commentSaved = commentStorage.get(commentId).get();

        return commentSaved;
    }

    private void checkPostExist(Long postId) {
        if (!commentStorage.checkPostExist(postId)) {
            throw new NotFoundException("Публикация с таким id не найдена");
        }
    }
}