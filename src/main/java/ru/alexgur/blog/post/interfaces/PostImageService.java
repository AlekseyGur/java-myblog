package ru.alexgur.blog.post.interfaces;

import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import ru.alexgur.blog.post.model.Image;

public interface PostImageService {
    Optional<Image> load(Long postId);

    void save(Long postId, MultipartFile image);

    String getImageUrl(Long postId);
}