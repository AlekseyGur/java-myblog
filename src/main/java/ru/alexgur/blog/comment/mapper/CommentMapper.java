package ru.alexgur.blog.comment.mapper;

import java.util.List;

import lombok.experimental.UtilityClass;
import ru.alexgur.blog.comment.dto.CommentDto;
import ru.alexgur.blog.comment.model.Comment;

@UtilityClass
public class CommentMapper {
    public static CommentDto toDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setPostId(comment.getPostId());
        return commentDto;
    }

    public static Comment toComment(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setText(commentDto.getText());
        comment.setPostId(commentDto.getPostId());
        return comment;
    }

    public static List<Comment> toComment(List<CommentDto> commentsDto) {
        return commentsDto.stream().map(CommentMapper::toComment).toList();
    }

    public static List<CommentDto> toDto(List<Comment> comments) {
        return comments.stream().map(CommentMapper::toDto).toList();
    }
}