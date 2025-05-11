package ru.alexgur.blog.post.interfaces;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ru.alexgur.blog.post.model.Post;

public interface PostRepository {
    Optional<Post> add(Post post);

    Optional<Post> get(Long id);

    Page<Post> find(String search, Pageable pageable);

    Page<Post> getAll(Pageable pageable);

    Optional<Post> update(Post post);

    void delete(Long id);

    Optional<Post> like(Long id);

    Optional<Post> dislike(Long id);

    boolean checkIdExist(Long id);

    Long getTotal();

    Page<Post> getByIds(List<Long> postIds, Pageable pageable);
}