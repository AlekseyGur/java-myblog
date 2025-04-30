package ru.alexgur.blog.post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import ru.alexgur.blog.post.interfaces.PostRepository;
import ru.alexgur.blog.post.mapper.PostRowMapper;
import ru.alexgur.blog.post.model.Post;
import ru.alexgur.blog.system.repository.BaseRepository;

@Repository
public class PostRepositoryImpl extends BaseRepository<Post> implements PostRepository {

    private static final String POST_ADD = "INSERT INTO posts(title, text) VALUES (?, ?);";
    private static final String POST_CHECK_ID_EXIST = "SELECT id FROM posts WHERE id = ?;";
    private static final String POST_GET_BY_ID = "SELECT * FROM posts WHERE id = ?;";
    private static final String POST_GET_ALL = "SELECT * FROM posts LIMIT ? OFFSET ?;";
    private static final String POST_UPDATE = "UPDATE posts SET title = ?, text = ? WHERE id = ? LIMIT 1;";
    private static final String POST_DELETE = "DELETE FROM posts WHERE id = ? LIMIT 1;";

    private static final String POST_LIKE = "UPDATE posts SET likes = likes + 1 WHERE id = ? LIMIT 1;";
    private static final String POST_DISLIKE = "UPDATE posts SET likes = likes - 1 WHERE id = ? LIMIT 1;";

    private static final String POST_FIND = """
            SELECT *
            FROM posts
            WHERE title LIKE ?
            OR text LIKE ?
            LIMIT ? OFFSET ?;""";

    @Autowired
    public PostRepositoryImpl(NamedParameterJdbcTemplate njdbc, PostRowMapper mapper) {
        super(njdbc, mapper);
    }

    @Override
    public Optional<Post> add(Post post) {
        long id = insert(POST_ADD,
                post.getTitle(),
                post.getText());

        return getPostImpl(id);
    }

    @Override
    public Optional<Post> get(Long id) {
        return getPostImpl(id);
    }

    @Override
    public List<Post> getAll(Integer offset, Integer limit) {
        return findMany(POST_GET_ALL, limit, offset);
    }

    @Override
    public List<Post> find(String search, Integer offset, Integer limit) {
        String q = "%" + search + "%";
        return findMany(POST_FIND, q, q, limit, offset);
    }

    @Override
    public Optional<Post> update(Post post) {
        update(POST_UPDATE,
                post.getTitle(),
                post.getText(),
                post.getId());

        return getPostImpl(post.getId());
    }

    @Override
    public void delete(Long id) {
        delete(POST_DELETE, id);
    }

    @Override
    public Optional<Post> like(Long id) {
        update(POST_LIKE, id);
        return getPostImpl(id);
    }

    @Override
    public Optional<Post> dislike(Long id) {
        update(POST_DISLIKE, id);
        return getPostImpl(id);
    }

    @Override
    public boolean checkIdExist(Long id) {
        return checkIdExist(POST_CHECK_ID_EXIST, id);
    }

    private Optional<Post> getPostImpl(Long id) {
        return findOne(POST_GET_BY_ID, id);
    }
}