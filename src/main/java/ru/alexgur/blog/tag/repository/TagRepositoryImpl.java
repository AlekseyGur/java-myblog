package ru.alexgur.blog.tag.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import ru.alexgur.blog.tag.dto.PairIdsDto;
import ru.alexgur.blog.tag.interfaces.TagRepository;
import ru.alexgur.blog.tag.mapper.PairIdsDtoRowMapper;
import ru.alexgur.blog.tag.mapper.TagRowMapper;
import ru.alexgur.blog.tag.model.Tag;
import ru.alexgur.blog.system.repository.BaseRepository;

@Repository
public class TagRepositoryImpl extends BaseRepository<Tag> implements TagRepository {
    private static final String TAG_ADD_MANY = "INSERT INTO tags(name) VALUES (:name);";
    private static final String TAG_GET_BY_NAME = "SELECT * FROM tags WHERE name in (:name);";
    private static final String TAG_GET_BY_ID = "SELECT * FROM tags WHERE id in (:ids);";

    private static final String TAG_GET_BY_POST_ID = "SELECT t.* FROM tags AS t JOIN tags_post p ON t.id = p.tag_id WHERE p.post_id = ?;";

    private static final String DELETE_BY_POST_ID = "DELETE FROM tags_post WHERE post_id = ?;";

    private static final String TAG_POST_PAIR = """
                SELECT
                    tp.post_id,
                    t.id as tag_id,
                    t.name as tag_name
                FROM tags_post tp
                JOIN tags t ON tp.tag_id = t.id
                WHERE tp.post_id IN (:postIds);
            """;

    private static final String TAG_ADD_MANY_POST = "INSERT INTO tags_post(tag_id, post_id) VALUES (:tagId, :postId)";

    public TagRepositoryImpl(NamedParameterJdbcTemplate njdbc, TagRowMapper mapper) {
        super(njdbc, mapper);
    }

    @Override
    public List<Tag> add(List<String> tags) {
        List<SqlParameterSource> paramsList = tags.stream()
                .map(tag -> new MapSqlParameterSource()
                        .addValue("name", tag))
                .collect(Collectors.toList());

        insertMany(TAG_ADD_MANY, paramsList.toArray(new SqlParameterSource[0]));

        return getByNameImpl(tags);
    }

    @Override
    public List<Tag> addTagsIdsToPost(Long postId, List<Long> tags) {
        List<SqlParameterSource> paramsList = tags.stream()
                .map(tagId -> new MapSqlParameterSource()
                        .addValue("postId", postId)
                        .addValue("tagId", tagId))
                .collect(Collectors.toList());

        insertMany(TAG_ADD_MANY_POST, paramsList.toArray(new SqlParameterSource[0]));

        return getByPostIdImpl(postId);
    }

    @Override
    public List<Tag> getById(List<Long> tagsIds) {
        SqlParameterSource parameters = new MapSqlParameterSource("ids", tagsIds);
        return findMany(TAG_GET_BY_ID, parameters);
    }

    @Override
    public List<PairIdsDto> getPostIdTagIdPair(List<Long> postIds) {
        final RowMapper<PairIdsDto> pairMapper = new PairIdsDtoRowMapper();
        SqlParameterSource parameters = new MapSqlParameterSource("postIds", postIds);
        return findManyIdToId(TAG_POST_PAIR, pairMapper, parameters);
    }

    @Override
    public List<Tag> getByName(List<String> tags) {
        return getByNameImpl(tags);
    }

    @Override
    public List<Tag> getByPostId(Long postId) {
        return getByPostIdImpl(postId);
    }

    @Override
    public void deleteByPostId(Long postId) {
        delete(DELETE_BY_POST_ID, postId);
    }

    private List<Tag> getByPostIdImpl(Long postId) {
        return findMany(TAG_GET_BY_POST_ID, postId);
    }

    private List<Tag> getByNameImpl(List<String> tags) {
        SqlParameterSource parameters = new MapSqlParameterSource("name", tags);
        return findMany(TAG_GET_BY_NAME, parameters);
    }
}