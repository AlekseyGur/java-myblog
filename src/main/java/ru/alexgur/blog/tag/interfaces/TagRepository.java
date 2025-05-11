package ru.alexgur.blog.tag.interfaces;

import java.util.List;

import ru.alexgur.blog.tag.dto.PairIdsDto;
import ru.alexgur.blog.tag.model.Tag;

public interface TagRepository {
    List<Tag> add(List<String> tags);

    List<Tag> getByName(List<String> tags);

    List<PairIdsDto> getPostIdTagIdPair(List<Long> postIds);

    List<Tag> getById(List<Long> tagsIds);

    List<Tag> getByPostId(Long postId);

    List<Tag> addTagsIdsToPost(Long postId, List<Long> tagsIds);

    void deleteByPostId(Long postId);
}