package ru.alexgur.blog.tag.interfaces;

import java.util.List;

import ru.alexgur.blog.tag.dto.TagDto;

public interface TagService {
    List<TagDto> add(Long postId, List<String> tags);

    List<TagDto> getByName(List<String> tags);

    List<TagDto> getByPostId(Long postId);

}