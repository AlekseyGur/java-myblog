package ru.alexgur.blog.post.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import ru.alexgur.blog.post.interfaces.PostImageService;
import ru.alexgur.blog.post.model.Image;

@Service
@RequiredArgsConstructor
public class PostImageServiceImpl implements PostImageService {
    @Value("${upload.dir}")
    private String UPLOAD_DIR;

    @Override
    public void save(Long postId, MultipartFile image) {
        String fullPath = getImageUrl(postId);
        try {
            Path path = Paths.get(UPLOAD_DIR);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            image.transferTo(new File(fullPath));

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении изображения", e);
        }
    }

    @Override
    public Optional<Image> load(Long postId) {
        String fullPath = getImageUrl(postId);
        try {
            File file = new File(fullPath);
            if (!file.exists()) {
                return Optional.ofNullable(null);
            }

            Image img = new Image(
                    Files.probeContentType(Paths.get(fullPath)),
                    file.length(),
                    new FileInputStream(file));

            return Optional.of(img);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении изображения", e);
        }
    }

    private String getImageUrl(Long postId) {
        return UPLOAD_DIR + "/" + postId;
    }
}