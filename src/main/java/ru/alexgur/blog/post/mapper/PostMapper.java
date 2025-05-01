package ru.alexgur.blog.post.mapper;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.experimental.UtilityClass;
import ru.alexgur.blog.post.dto.PostDto;
import ru.alexgur.blog.post.model.Post;

@UtilityClass
public class PostMapper {
    public static PostDto toDto(Post post) {
        PostDto postDto = new PostDto();
        postDto.setId(post.getId());
        postDto.setTitle(post.getTitle());
        postDto.setText(post.getText());
        postDto.setLikes(post.getLikes());
        return postDto;
    }

    public static Post toPost(PostDto postDto) {
        Post post = new Post();
        post.setId(postDto.getId());
        post.setTitle(postDto.getTitle());
        post.setText(postDto.getText());
        post.setLikes(postDto.getLikes());
        return post;
    }

    public static List<Post> toPost(List<PostDto> postsDto) {
        return postsDto.stream().map(PostMapper::toPost).toList();
    }

    public static List<PostDto> toDto(List<Post> posts) {
        return posts.stream().map(PostMapper::toDto).toList();
    }

    public static Page<PostDto> toDto(Page<Post> posts) {
        return posts.map(PostMapper::toDto);
    }
}