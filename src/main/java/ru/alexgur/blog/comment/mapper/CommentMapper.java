package ru.alexgur.blog.comment.mapper;

import java.util.List;

import lombok.experimental.UtilityClass;
import ru.alexgur.blog.comment.dto.CommentDto;
import ru.alexgur.blog.comment.model.Comment;

@UtilityClass
public class CommentMapper {
    public static CommentDto commentToDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setName(comment.getName());
        commentDto.setDescription(comment.getDescription());
        commentDto.setAvailable(comment.getAvailable());
        commentDto.setOwner(comment.getOwner());
        if (comment.getRequest() != null) {
            commentDto.setRequest(comment.getRequest());
        }
        return commentDto;
    }

    public static Comment dtoToComment(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setName(commentDto.getName());
        comment.setDescription(commentDto.getDescription());
        comment.setAvailable(commentDto.getAvailable());
        comment.setOwner(commentDto.getOwner());
        return comment;
    }

    public static List<Comment> dtoToComment(List<CommentDto> commentsDto) {
        return commentsDto.stream().map(CommentMapper::dtoToComment).toList();
    }

    public static List<CommentDto> commentToDto(List<Comment> comments) {
        return comments.stream().map(CommentMapper::commentToDto).toList();
    }
}