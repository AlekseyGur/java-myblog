package ru.alexgur.blog.post.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.alexgur.blog.comment.dto.CommentDto;
import ru.alexgur.blog.tag.dto.TagDto;

@Data
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
}