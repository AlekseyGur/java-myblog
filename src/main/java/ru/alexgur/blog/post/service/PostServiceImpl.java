package ru.alexgur.blog.post.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import ru.alexgur.blog.system.exception.NotFoundException;
import ru.alexgur.blog.tag.dto.TagDto;
import ru.alexgur.blog.tag.interfaces.TagService;
import ru.alexgur.blog.comment.dto.CommentDto;
import ru.alexgur.blog.comment.interfaces.CommentService;
import ru.alexgur.blog.post.dto.PostDto;
import ru.alexgur.blog.post.interfaces.PostService;
import ru.alexgur.blog.post.interfaces.PostImageService;
import ru.alexgur.blog.post.interfaces.PostRepository;
import ru.alexgur.blog.post.mapper.PostMapper;
import ru.alexgur.blog.post.model.Post;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postStorage;
    private final TagService tagsService;
    private final CommentService commentsService;
    private final PostImageService postImageService;

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
                .map(this::addPostInfo)
                .orElseThrow(() -> new NotFoundException("Публикация с таким id не найдена"));
    }

    @Override
    public Page<PostDto> find(String search, Pageable pageable) {
        Page<PostDto> postSaved = PostMapper.toDto(postStorage.find(search, pageable));
        return addPostInfo(postSaved);
    }

    @Override
    public Page<PostDto> getAll(Pageable pageable) {
        Page<PostDto> postSaved = PostMapper.toDto(postStorage.getAll(pageable));
        return addPostInfo(postSaved);
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

        if (image != null && !image.isEmpty()) {
            saveImage(postSaved.getId(), image);
        }

        if (!tags.isBlank()) {
            saveTags(postSaved.getId(), tags);
        }

        return PostMapper.toDto(postStorage.update(postSaved).orElse(null));
    }

    private void saveImage(Long postId, MultipartFile image) {
        postImageService.save(postId, image);
    }

    private void saveTags(Long id, String tags) {
        List<String> tagsList = Arrays.asList(tags.split(",")).stream()
                .map(String::strip)
                .distinct()
                .toList();
        tagsService.add(id, tagsList);
    }

    private PostDto getImpl(Long id) {
        PostDto postSaved = PostMapper.toDto(postStorage.get(id).orElse(null));
        return addPostInfo(postSaved);
    }

    private List<PostDto> addPostInfo(List<PostDto> posts) {
        List<Long> postsIds = posts.stream().map(PostDto::getId).toList();

        Map<Long, List<TagDto>> tags = tagsService.getByPostId(postsIds);
        Map<Long, List<CommentDto>> comments = commentsService.getByPostId(postsIds);

        return posts.stream()
                .map(x -> {
                    Long postId = x.getId();
                    x.setUrl(getPostUrl(postId));
                    x.setTags(tags.getOrDefault(postId, List.of()));
                    x.setComments(comments.getOrDefault(postId, List.of()));
                    x.setImageUrl(postImageService.getImageUrl(postId));
                    return x;
                }).toList();
    }

    private PostDto addPostInfo(PostDto post) {
        return addPostInfo(List.of(post)).get(0);
    }

    private Page<PostDto> addPostInfo(Page<PostDto> posts) {
        return new PageImpl<>(addPostInfo(posts.getContent()),
                posts.getPageable(),
                posts.getTotalElements());
    }

    private String getPostUrl(Long postId) {
        return "/posts/" + postId;
    }
}