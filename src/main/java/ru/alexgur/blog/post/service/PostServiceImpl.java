package ru.alexgur.blog.post.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final TagService tagsService;
    private final CommentService commentsService;
    private final PostImageService postImageService;

    @Override
    @Transactional
    public PostDto add(String title, String text, String tags, MultipartFile image) {
        Post post = new Post();
        post.setTitle(title);
        post.setText(text);
        Post postSaved = postRepository.add(post).orElse(null);

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
        Page<PostDto> postSaved = PostMapper.toDto(postRepository.find(search, pageable));
        return addPostInfo(postSaved);
    }

    @Override
    public Page<PostDto> getAll(Pageable pageable) {
        Page<PostDto> postSaved = PostMapper.toDto(postRepository.getAll(pageable));

        System.err.println(postSaved.toString());

        return addPostInfo(postSaved);
    }

    @Override
    public Page<PostDto> getByTagName(String tag, Pageable pageable) {
        List<Long> postIds = tagsService.getPostsIdsByTagName(tag);
        if (postIds.isEmpty()) {
            return getZeroResultsPage(pageable);
        } else {
            Page<PostDto> postSaved = PostMapper.toDto(postRepository.getByIds(postIds, pageable));
            return addPostInfo(postSaved);
        }
    }

    @Override
    @Transactional
    public void like(Long id, boolean isLike) {
        if (isLike) {
            postRepository.like(id);
        } else {
            postRepository.dislike(id);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        postRepository.delete(id);
    }

    @Override
    public boolean checkIdExist(Long id) {
        return postRepository.checkIdExist(id);
    }

    @Override
    @Transactional
    public PostDto patch(Long id, String title, String text, String tags, MultipartFile image) {
        Post postSaved = postRepository.get(id).orElse(null);

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
            removeTags(postSaved.getId());
            saveTags(postSaved.getId(), tags);
        }

        return PostMapper.toDto(postRepository.update(postSaved).orElse(null));
    }

    private Page<PostDto> getZeroResultsPage(Pageable pageable) {
        return new PageImpl<>(List.of(), pageable, 0);
    }

    private void saveImage(Long postId, MultipartFile image) {
        postImageService.save(postId, image);
    }

    @Transactional
    private void removeTags(Long postId) {
        tagsService.deleteByPostId(postId);
    }

    @Transactional
    private void saveTags(Long id, String tags) {
        List<String> tagsList = Arrays.asList(tags.split(",")).stream()
                .map(String::strip)
                .distinct()
                .toList();
        tagsService.add(id, tagsList);
    }

    private PostDto getImpl(Long id) {
        PostDto postSaved = PostMapper.toDto(postRepository.get(id).orElse(null));
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