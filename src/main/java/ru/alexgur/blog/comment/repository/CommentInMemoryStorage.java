package ru.alexgur.blog.comment.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import ru.alexgur.blog.comment.model.Comment;
import ru.alexgur.blog.comment.interfaces.CommentStorage;

@Repository
public class CommentInMemoryStorage implements CommentStorage {
    public static Long commentId = 0L;
    private static final HashMap<Long, Comment> comments = new HashMap<>();

    @Override
    public Optional<Comment> add(Comment comment) {
        Long id = getNextId();
        comment.setId(id);
        comments.put(id, comment);
        return getImpl(id);
    }

    @Override
    public Optional<Comment> get(Long id) {
        return getImpl(id);
    }

    @Override
    public List<Comment> find(String query) {
        String q = query.toLowerCase();
        return comments.values().stream()
                .filter(x -> x.getAvailable())
                .filter(x -> x.getName().toLowerCase().contains(q)
                        || x.getDescription().toLowerCase().contains(q))
                .toList();
    }

    @Override
    public List<Comment> getByUserId(Long userId) {
        return comments.values().stream().filter(x -> x.getOwner().equals(userId)).toList();
    }

    @Override
    public Optional<Comment> update(Comment comment) {
        comments.put(comment.getId(), comment);
        return getImpl(comment.getId());
    }

    @Override
    public void delete(Long id) {
        if (comments.containsKey(id)) {
            comments.remove(id);
        }
    }

    @Override
    public boolean checkIdExist(Long id) {
        return comments.containsKey(id);
    }

    private Optional<Comment> getImpl(Long id) {
        return Optional.ofNullable(comments.getOrDefault(id, null));
    }

    private Long getNextId() {
        return ++commentId;
    }
}