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
import ru.alexgur.blog.system.exception.ConstraintViolationException;

@Service
@RequiredArgsConstructor
public class PostImageServiceImpl implements PostImageService {
    @Value("${upload.images.base-url}")
    private String UPLOAD_URL_DIR;

    @Value("${upload.images.dir}")
    private String UPLOAD_DIR;

    @Value("${upload.images.format}")
    private String UPLOAD_IMAGE_FORMAT;

    @Override
    public void save(Long postId, MultipartFile image) {
        if (image == null) {
            return;
        }

        validateImageThrowError(image);

        try {
            Path path = Paths.get(UPLOAD_DIR);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            String fullPath = getImageUrlImpl(path.toString(), postId);
            image.transferTo(new File(fullPath));

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении изображения", e);
        }
    }

    @Override
    public Optional<Image> load(Long postId) {
        try {
            Path path = Paths.get(UPLOAD_DIR);
            String fullPath = getImageUrlImpl(path.toString(), postId);

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

    @Override
    public String getImageUrl(Long postId) {
        return getImageUrlImpl(UPLOAD_URL_DIR, postId);
    }

    private String getImageUrlImpl(String baseDir, Long postId) {
        return baseDir + "/" + postId.toString() + "." + UPLOAD_IMAGE_FORMAT;
    }

    private void validateImageThrowError(MultipartFile image) {
        String fileName = image != null ? image.getOriginalFilename() : "";
        if (fileName == null || fileName.isBlank() || !fileName.endsWith("." + UPLOAD_IMAGE_FORMAT)) {
            throw new ConstraintViolationException("Файл должен иметь расширение " + UPLOAD_IMAGE_FORMAT);
        }
    }
}