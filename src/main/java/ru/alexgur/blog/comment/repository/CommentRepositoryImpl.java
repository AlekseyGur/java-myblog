package ru.alexgur.blog.comment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import ru.alexgur.blog.comment.interfaces.CommentRepository;
import ru.alexgur.blog.comment.mapper.CommentRowMapper;
import ru.alexgur.blog.comment.model.Comment;
import ru.alexgur.blog.system.repository.BaseRepository;

@Repository
public class CommentRepositoryImpl extends BaseRepository<Comment> implements CommentRepository {

    private static final String COMMENT_ADD = "INSERT INTO comments(text, post_id) VALUES (?, ?);";
    private static final String COMMENT_CHECK_ID_EXIST = "SELECT id FROM comments WHERE id = ?;";
    private static final String COMMENT_CHECK_POST_EXIST = "SELECT id FROM posts WHERE id = ?;";
    private static final String COMMENT_GET_BY_ID = "SELECT * FROM comments WHERE id = ?;";
    private static final String COMMENT_GET_BY_POST_ID = "SELECT * FROM comments WHERE post_id = ?;";
    private static final String COMMENT_UPDATE = "UPDATE comments SET text = ?, text = ? WHERE id = ? LIMIT 1;";
    private static final String COMMENT_DELETE = "DELETE FROM comments WHERE id = ? LIMIT 1;";

    public CommentRepositoryImpl(NamedParameterJdbcTemplate njdbc, CommentRowMapper mapper) {
        super(njdbc, mapper);
    }

    @Override
    public Optional<Comment> add(Comment comment) {
        long id = insert(COMMENT_ADD,
                comment.getText(),
                comment.getPostId());

        return getCommentImpl(id);
    }

    @Override
    public Optional<Comment> get(Long id) {
        return getCommentImpl(id);
    }

    @Override
    public List<Comment> getByPostId(Long postId) {
        return findMany(COMMENT_GET_BY_POST_ID, postId);
    }

    @Override
    public Optional<Comment> update(Comment comment) {
        update(COMMENT_UPDATE,
                comment.getText(),
                comment.getPostId(),
                comment.getId());

        return getCommentImpl(comment.getId());
    }

    @Override
    public void delete(Long id) {
        delete(COMMENT_DELETE, id);
    }

    @Override
    public boolean checkIdExist(Long id) {
        return checkIdExist(COMMENT_CHECK_ID_EXIST, id);
    }

    @Override
    public boolean checkPostExist(Long id) {
        return checkIdExist(COMMENT_CHECK_POST_EXIST, id);
    }

    private Optional<Comment> getCommentImpl(Long id) {
        return findOne(COMMENT_GET_BY_ID, id);
    }
}