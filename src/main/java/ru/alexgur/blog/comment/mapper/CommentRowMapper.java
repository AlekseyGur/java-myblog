package ru.alexgur.blog.comment.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import ru.alexgur.blog.comment.model.Comment;

@Component
public class CommentRowMapper implements RowMapper<Comment> {
    @SuppressWarnings("null")
    @Override
    @Nullable
    public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
        Comment comment = new Comment();
        comment.setId(rs.getLong("id"));
        comment.setText(rs.getString("text"));
        comment.setPostId(rs.getLong("post_id"));
        return comment;
    }
}