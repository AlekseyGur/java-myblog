package ru.alexgur.blog.post.mapper;

import java.util.List;

import lombok.experimental.UtilityClass;
import ru.alexgur.blog.post.dto.PostDto;
import ru.alexgur.blog.post.model.Post;

@UtilityClass
public class PostMapper {
    public static PostDto postToDto(Post post) {
        PostDto postDto = new PostDto();
        postDto.setId(post.getId());
        postDto.setTitle(post.getTitle());
        postDto.setTextPreview(post.getTextPreview());
        postDto.setTextDetail(post.getTextDetail());
        postDto.setLikes(post.getLikes());
        return postDto;
    }

    public static Post dtoToPost(PostDto postDto) {
        Post post = new Post();
        post.setId(postDto.getId());
        post.setTitle(postDto.getTitle());
        post.setTextPreview(postDto.getTextPreview());
        post.setTextDetail(postDto.getTextDetail());
        post.setLikes(postDto.getLikes());
        return post;
    }

    public static List<Post> dtoToPost(List<PostDto> postsDto) {
        return postsDto.stream().map(PostMapper::dtoToPost).toList();
    }

    public static List<PostDto> postToDto(List<Post> posts) {
        return posts.stream().map(PostMapper::postToDto).toList();
    }
}