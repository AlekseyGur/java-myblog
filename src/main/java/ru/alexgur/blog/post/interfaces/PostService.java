package ru.alexgur.blog.post.interfaces;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import ru.alexgur.blog.post.dto.PostDto;

public interface PostService {
    PostDto add(String title, String text, String tags, MultipartFile image);

    PostDto get(Long id);

    List<PostDto> getAll(String search, Integer pageSize, Integer pageNumber);

    void delete(Long id);

    void like(Long id, boolean isLike);

    PostDto patch(Long id, String title, String text, String tags, MultipartFile image);

    boolean checkIdExist(Long id);
}