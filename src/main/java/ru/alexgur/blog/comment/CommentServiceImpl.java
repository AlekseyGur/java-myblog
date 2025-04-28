package ru.alexgur.blog.comment;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.alexgur.blog.system.exception.AccessDeniedException;
import ru.alexgur.blog.system.exception.NotFoundException;
import ru.alexgur.blog.comment.dto.CommentDto;
import ru.alexgur.blog.comment.interfaces.CommentService;
import ru.alexgur.blog.comment.interfaces.CommentStorage;
import ru.alexgur.blog.comment.mapper.CommentMapper;
import ru.alexgur.blog.comment.model.Comment;
import ru.alexgur.blog.post.interfaces.PostStorage;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentStorage commentStorage;
    private final PostStorage postService;

    @Override
    public CommentDto add(CommentDto commentDto) {
        Comment comment = CommentMapper.dtoToComment(commentDto);
        if (!postService.checkIdExist(comment.getOwner())) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        return CommentMapper.commentToDto(commentStorage.add(comment).orElse(null));
    }

    @Override
    public CommentDto get(Long id) {
        return commentStorage.get(id)
                .map(CommentMapper::commentToDto)
                .orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена"));
    }

    @Override
    public List<CommentDto> find(String query) {
        if (query.isBlank()) {
            return List.of();
        }
        return CommentMapper.commentToDto(commentStorage.find(query));
    }

    @Override
    public List<CommentDto> getByUserId(Long userId) {
        return CommentMapper.commentToDto(commentStorage.getByUserId(userId));
    }

    @Override
    public void delete(Long id) {
        commentStorage.delete(id);
    }

    @Override
    public CommentDto patch(CommentDto commentDto) {
        Comment commentSaved = checkAccess(commentDto);

        if (commentDto.getName() != null) {
            commentSaved.setName(commentDto.getName());
        }

        if (commentDto.getDescription() != null) {
            commentSaved.setDescription(commentDto.getDescription());
        }

        if (commentDto.getRequest() != null) {
            commentSaved.setRequest(commentDto.getRequest());
        }

        if (commentDto.getAvailable() != null) {
            commentSaved.setAvailable(commentDto.getAvailable());
        }

        return CommentMapper.commentToDto(commentStorage.update(commentSaved).orElse(null));
    }

    @Override
    public boolean checkIdExist(Long id) {
        return commentStorage.checkIdExist(id);
    }

    private Comment checkAccess(CommentDto comment) {
        Long commentId = comment.getId();
        Long userId = comment.getOwner();

        if (comment == null || !checkIdExist(commentId)) {
            throw new NotFoundException("Вещь с таким id не найдена");
        }

        if (!postService.checkIdExist(userId)) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }

        Comment commentSaved = commentStorage.get(commentId).get();

        if (!commentSaved.getOwner().equals(userId)) {
            throw new AccessDeniedException("Только владелец может редактировать вещь");
        }

        return commentSaved;
    }

}