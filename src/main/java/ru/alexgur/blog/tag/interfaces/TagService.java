package ru.alexgur.blog.tag.interfaces;

import java.util.List;
import java.util.Map;

import ru.alexgur.blog.tag.dto.TagDto;

public interface TagService {
    List<TagDto> add(Long postId, List<String> tags);

    List<TagDto> getByName(List<String> tags);

    List<TagDto> getById(List<Long> tagsIds);

    List<TagDto> getByPostId(Long postId);

    Map<Long, List<TagDto>> getByPostId(List<Long> postIds);

    List<Long> getPostsIdsByTagName(String tag);

    void deleteByPostId(Long postId);
}