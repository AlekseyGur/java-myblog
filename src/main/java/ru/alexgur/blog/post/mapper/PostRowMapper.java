package ru.alexgur.blog.post.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import ru.alexgur.blog.post.model.Post;

@Component
public class PostRowMapper implements RowMapper<Post> {
    @SuppressWarnings("null")
    @Override
    @Nullable
    public Post mapRow(ResultSet rs, int rowNum) throws SQLException {
        Post post = new Post();
        post.setId(rs.getLong("id"));
        post.setTitle(rs.getString("title"));
        post.setTextPreview(rs.getString("text_preview"));
        post.setTextDetail(rs.getString("text_detail"));
        post.setLikes(rs.getInt("likes"));
        return post;
    }
}