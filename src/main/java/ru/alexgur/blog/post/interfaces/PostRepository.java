package ru.alexgur.blog.post.interfaces;

import java.util.List;
import java.util.Optional;

import ru.alexgur.blog.post.model.Post;

public interface PostRepository {
    Optional<Post> add(Post post);

    Optional<Post> get(Long id);

    List<Post> find(String search, Integer offset, Integer limit);

    List<Post> getAll(Integer offset, Integer limit);

    Optional<Post> update(Post post);

    void delete(Long id);

    Optional<Post> like(Long id);

    Optional<Post> dislike(Long id);

    boolean checkIdExist(Long id);
}