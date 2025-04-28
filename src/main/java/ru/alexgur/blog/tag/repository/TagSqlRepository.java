package ru.alexgur.blog.tag.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import ru.alexgur.blog.tag.interfaces.TagStorage;
import ru.alexgur.blog.tag.mapper.TagRowMapper;
import ru.alexgur.blog.tag.model.Tag;
import ru.alexgur.blog.system.repository.BaseRepository;

@Repository
public class TagSqlRepository extends BaseRepository<Tag> implements TagStorage {

    private static final String TAG_ADD_MANY = "INSERT INTO tags(name) VALUES :name;";
    private static final String TAG_GET_BY_NAME = "SELECT * FROM tags WHERE NAME in :name;";

    private static final String TAG_GET_BY_POST_ID = "SELECT t.* FROM tags AS t JOIN tags_post p ON t.id = p.tag_id WHERE p.post_id = ?;";

    private static final String TAG_ADD_MANY_POST = "INSERT INTO films_genres(tag_id, post_id) VALUES (:tagId, :postId)";

    @Autowired
    public TagSqlRepository(NamedParameterJdbcTemplate njdbc, TagRowMapper mapper) {
        super(njdbc, mapper);
    }

    @Override
    public List<Tag> add(List<String> tags) {
        List<SqlParameterSource> paramsList = tags.stream()
                .map(tag -> new MapSqlParameterSource()
                        .addValue("name", tag))
                .collect(Collectors.toList());

        insertMany(TAG_ADD_MANY, paramsList.toArray(new SqlParameterSource[0]));

        return findMany(TAG_GET_BY_NAME, tags);
    }

    @Override
    public List<Tag> addPost(Long postId, List<Tag> tags) {
        List<SqlParameterSource> paramsList = tags.stream()
                .map(tag -> new MapSqlParameterSource()
                        .addValue("postId", postId)
                        .addValue("tagId", tag.getId()))
                .collect(Collectors.toList());

        insertMany(TAG_ADD_MANY_POST, paramsList.toArray(new SqlParameterSource[0]));

        return getByPostIdImpl(postId);
    }

    @Override
    public List<Tag> getByName(List<String> tags) {
        return findMany(TAG_GET_BY_NAME, tags);
    }

    @Override
    public List<Tag> getByPostId(Long postId) {
        return getByPostIdImpl(postId);
    }

    private List<Tag> getByPostIdImpl(Long postId) {
        return findMany(TAG_GET_BY_POST_ID, postId);
    }
}