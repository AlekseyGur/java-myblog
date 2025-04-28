package ru.alexgur.blog.comment;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.alexgur.blog.system.exception.NotFoundException;
import ru.alexgur.blog.comment.dto.CommentDto;
import ru.alexgur.blog.comment.interfaces.CommentService;
import ru.alexgur.blog.comment.interfaces.CommentStorage;
import ru.alexgur.blog.comment.mapper.CommentMapper;
import ru.alexgur.blog.comment.model.Comment;
import ru.alexgur.blog.post.interfaces.PostService;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentStorage commentStorage;
    private final PostService postService;

    @Override
    public CommentDto add(CommentDto commentDto) {
        Comment comment = CommentMapper.dtoToComment(commentDto);

        if (!postService.checkIdExist(comment.getPostId())) {
            throw new NotFoundException("Публикация с таким id не найдена");
        }

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

        if (!postService.checkIdExist(comment.getPostId())) {
            throw new NotFoundException("Публикация с таким id не найдена");
        }

        Comment commentSaved = commentStorage.get(commentId).get();

        return commentSaved;
    }

}