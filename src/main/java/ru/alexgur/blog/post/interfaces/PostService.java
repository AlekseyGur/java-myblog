package ru.alexgur.blog.post.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import ru.alexgur.blog.post.dto.PostDto;

public interface PostService {
    PostDto add(String title, String text, String tags, MultipartFile image);

    PostDto get(Long id);

    Page<PostDto> find(String search, Pageable pageable);

    Page<PostDto> getByTagName(String search, Pageable pageable);

    Page<PostDto> getAll(Pageable pageable);

    void delete(Long id);

    void like(Long id, boolean isLike);

    PostDto patch(Long id, String title, String text, String tags, MultipartFile image);

    boolean checkIdExist(Long id);
}