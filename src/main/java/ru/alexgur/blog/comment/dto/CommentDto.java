package ru.alexgur.blog.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private Long postId;
    private String text;

    public String getUrl() {
        return "/posts/" + (getId() != null ? getId().toString() : "null");
    }
}