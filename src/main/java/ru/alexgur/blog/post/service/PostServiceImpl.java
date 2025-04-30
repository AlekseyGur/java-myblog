package ru.alexgur.blog.post.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import ru.alexgur.blog.system.exception.NotFoundException;
import ru.alexgur.blog.tag.interfaces.TagService;
import ru.alexgur.blog.comment.interfaces.CommentService;
import ru.alexgur.blog.post.dto.PostDto;
import ru.alexgur.blog.post.interfaces.PostService;
import ru.alexgur.blog.post.interfaces.PostRepository;
import ru.alexgur.blog.post.mapper.PostMapper;
import ru.alexgur.blog.post.model.Post;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final String UPLOAD_DIR = "/uploads";
    private final PostRepository postStorage;
    private final TagService tagsService;
    private final CommentService commentsService;

    @Override
    public PostDto add(String title, String text, String tags, MultipartFile image) {
        Post post = new Post();
        post.setTitle(title);
        post.setText(text);
        Post postSaved = postStorage.add(post).orElse(null);

        if (!image.isEmpty()) {
            saveImage(postSaved.getId(), image);
        }

        if (!tags.isBlank()) {
            saveTags(postSaved.getId(), tags);
        }

        return getImpl(postSaved.getId());
    }

    @Override
    public PostDto get(Long id) {
        return Optional.ofNullable(getImpl(id))
                .orElseThrow(() -> new NotFoundException("Публикация с таким id не найдена"));
    }

    @Override
    public List<PostDto> getAll(String search, Integer pageSize, Integer pageNumber) {
        if (search != null) {
            return PostMapper.postToDto(postStorage.find(search, pageSize, pageNumber));
        }
        return PostMapper.postToDto(postStorage.getAll(pageSize, pageNumber));
    }

    @Override
    public void like(Long id, boolean isLike) {
        if (isLike) {
            postStorage.like(id);
        } else {
            postStorage.dislike(id);
        }
    }

    @Override
    public void delete(Long id) {
        postStorage.delete(id);
    }

    @Override
    public boolean checkIdExist(Long id) {
        return postStorage.checkIdExist(id);
    }

    @Override
    public PostDto patch(Long id, String title, String text, String tags, MultipartFile image) {
        Post postSaved = postStorage.get(id).orElse(null);

        if (title != null) {
            postSaved.setTitle(title);
        }

        if (text != null) {
            postSaved.setText(text);
        }

        if (!image.isEmpty()) {
            saveImage(postSaved.getId(), image);
        }

        if (!tags.isBlank()) {
            saveTags(postSaved.getId(), tags);
        }

        return PostMapper.postToDto(postStorage.update(postSaved).orElse(null));
    }

    private void saveImage(Long id, MultipartFile image) {
        try {
            Path path = Paths.get("uploads");
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            String fullPath = getImageUrl(id);
            image.transferTo(new File(fullPath));

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении изображения", e);
        }
    }

    private void saveTags(Long id, String tags) {
        List<String> tagsList = Arrays.asList(tags.split(",")).stream()
                .map(String::strip)
                .distinct()
                .toList();
        tagsService.add(id, tagsList);
    }

    private PostDto getImpl(Long id) {
        PostDto postSaved = PostMapper.postToDto(postStorage.get(id).orElse(null));

        postSaved.setUrl(getImageUrl(id));

        postSaved.setTags(tagsService.getByPostId(id));

        postSaved.setComments(commentsService.getByPostId(id));

        return postSaved;
    }

    private String getImageUrl(Long postId) {
        return UPLOAD_DIR + "/" + postId;
    }
}