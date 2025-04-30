package ru.alexgur.blog.comment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import ru.alexgur.blog.comment.dto.CommentDto;
import ru.alexgur.blog.comment.interfaces.CommentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{postId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public String addComment(@RequestBody CommentDto commentDto) {
        CommentDto savedComment = commentService.add(commentDto);
        return "redirect:" + savedComment.getUrl();
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String editComment(@PathVariable Long id, @RequestBody CommentDto commentDto) {
        CommentDto savedComment = commentService.patch(commentDto);

        return "redirect:" + savedComment.getUrl();
    }

    @PostMapping(value = "/{postId}/comments/{commentId}/delete", params = "_method=delete")
    @ResponseStatus(HttpStatus.OK)
    public String deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
        commentService.delete(commentId);
        return "redirect:/posts/" + postId.toString();
    }
}