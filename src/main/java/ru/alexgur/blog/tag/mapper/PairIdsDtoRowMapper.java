package ru.alexgur.blog.tag.mapper;

import ru.alexgur.blog.tag.dto.PairIdsDto;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@Qualifier("PairIdsDtoRowMapper")
public class PairIdsDtoRowMapper implements RowMapper<PairIdsDto> {

    @SuppressWarnings("null")
    @Nullable
    @Override
    public PairIdsDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        PairIdsDto pair = new PairIdsDto();
        pair.setFirst(rs.getLong("post_id"));
        pair.setLast(rs.getLong("tag_id"));
        return pair;
    }
}
