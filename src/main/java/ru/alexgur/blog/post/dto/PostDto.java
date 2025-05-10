package ru.alexgur.blog.post.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import ru.alexgur.blog.comment.dto.CommentDto;
import ru.alexgur.blog.tag.dto.TagDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private Long id;
    private String title;
    private String text;

    private String url;
    private String imagePath;
    private Integer likes;

    private List<CommentDto> comments = List.of();
    private List<TagDto> tags = List.of();

    public List<String> getTags() {
        return tags.stream().map(TagDto::getName).toList();
    }

    public List<Long> getTagsIds() {
        return tags.stream().map(TagDto::getId).toList();
    }

    public List<Long> getCommentsIds() {
        return comments.stream().map(CommentDto::getId).toList();
    }

    public String getLikesCount() {
        return likes.toString();
    }

    public String getTextParts() {
        return text.length() > 200 ? text.substring(0, 200) : text;
    }

    public String getTextPreview() {
        return text.length() > 200 ? text.substring(0, 200) : text;
    }

    public String getCommentsSize() {
        return String.valueOf(comments != null ? comments.size() : 0);
    }
}