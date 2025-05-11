package ru.alexgur.blog.post.interfaces;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public interface PostImageService {
    ResponseEntity<StreamingResponseBody> load(Long postId);

    void save(Long postId, MultipartFile image);

    String getImageUrl(Long postId);
}