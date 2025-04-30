package ru.alexgur.blog.tag.interfaces;

import java.util.List;

import ru.alexgur.blog.tag.model.Tag;

public interface TagRepository {

    List<Tag> add(List<String> tags);

    List<Tag> getByName(List<String> tags);

    List<Tag> getByPostId(Long postId);

    List<Tag> addPost(Long postId, List<Tag> tags);
}