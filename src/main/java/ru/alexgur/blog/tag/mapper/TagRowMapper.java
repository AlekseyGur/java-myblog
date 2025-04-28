package ru.alexgur.blog.tag.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import ru.alexgur.blog.tag.model.Tag;

@Component
public class TagRowMapper implements RowMapper<Tag> {
    @SuppressWarnings("null")
    @Override
    @Nullable
    public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
        Tag post = new Tag();
        post.setId(rs.getLong("id"));
        post.setName(rs.getString("name"));
        return post;
    }
}