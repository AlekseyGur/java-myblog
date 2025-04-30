package ru.alexgur.blog.tag.mapper;

import ru.alexgur.blog.tag.model.Tag;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@Qualifier("tagRowMapper")
public class TagRowMapper implements RowMapper<Tag> {

    @SuppressWarnings("null")
    @Nullable
    @Override
    public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
        Tag tag = new Tag();
        tag.setId(rs.getLong("id"));
        tag.setName(rs.getString("name"));
        return tag;
    }
}
