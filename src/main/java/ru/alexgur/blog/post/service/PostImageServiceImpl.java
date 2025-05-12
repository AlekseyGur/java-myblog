package ru.alexgur.blog.post.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import lombok.RequiredArgsConstructor;
import ru.alexgur.blog.post.interfaces.PostImageService;
import ru.alexgur.blog.post.model.Image;
import ru.alexgur.blog.system.exception.ConstraintViolationException;
import ru.alexgur.blog.system.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class PostImageServiceImpl implements PostImageService {
    @SuppressWarnings("checkstyle:MemberName")
    @Value("${upload.images.base-url}")
    private String UPLOAD_URL_DIR;

    @SuppressWarnings("checkstyle:MemberName")
    @Value("${upload.images.dir}")
    private String UPLOAD_DIR;

    @SuppressWarnings("checkstyle:MemberName")
    @Value("${upload.images.format}")
    private String UPLOAD_IMAGE_FORMAT;

    @Override
    public ResponseEntity<StreamingResponseBody> load(Long postId) {
        Optional<Image> imgOpt = loadImageFile(postId);

        if (imgOpt.isEmpty()) {
            throw new NotFoundException("Файл не найден");
        }

        Image image = imgOpt.get();
        StreamingResponseBody body = outputStream -> {
            try (InputStream inputStream = image.getInputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                throw new RuntimeException("Ошибка при передаче изображения", e);
            }
        };

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(image.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + getImageFileName(postId))
                .header(HttpHeaders.CONTENT_LENGTH, image.getContentLength().toString())
                .body(body);
    }

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
    public String getImageUrl(Long postId) {
        return getImageUrlImpl(UPLOAD_URL_DIR, postId);
    }

    private Optional<Image> loadImageFile(Long postId) {
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

    private String getImageFileName(Long postId) {
        return postId.toString() + "." + UPLOAD_IMAGE_FORMAT;
    }

    private String getImageUrlImpl(String baseDir, Long postId) {
        return baseDir + "/" + getImageFileName(postId);
    }

    private void validateImageThrowError(MultipartFile image) {
        String fileName = image != null ? image.getOriginalFilename() : "";
        if (fileName == null || fileName.isBlank() || !fileName.endsWith("." + UPLOAD_IMAGE_FORMAT)) {
            throw new ConstraintViolationException("Файл должен иметь расширение " + UPLOAD_IMAGE_FORMAT);
        }
    }
}