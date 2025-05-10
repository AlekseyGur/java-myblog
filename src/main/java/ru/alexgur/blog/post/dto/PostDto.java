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
    private List<CommentDto> comments;
    private List<TagDto> tags;

    public String getTagsAsText() {
        return String.join(", ",
                tags.stream().map(x -> x.getName()).toList());
    }

    public String getLikesCount() {
        return likes.toString();
    }

    public String getTextParts() {
        return text.substring(0, 200);
    }

    public String getTextPreview() {
        return text.substring(0, 200);
    }

    public String getCommentsSize() {
        return String.valueOf(comments != null ? comments.size() : 0);
    }
}