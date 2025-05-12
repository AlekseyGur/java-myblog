package ru.alexgur.blog.comment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import lombok.RequiredArgsConstructor;
import ru.alexgur.blog.comment.dto.CommentDto;
import ru.alexgur.blog.comment.interfaces.CommentService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
@Validated
public class CommentController {
    private final CommentService commentService;

    @PostMapping(value = "/{postId}/comments")
    public String add(
            @PathVariable(name = "postId") Long postId,
            @RequestParam(value = "text") String text) {
        commentService.add(postId, text);
        return "redirect:/posts/" + postId.toString();
    }

    @PostMapping(value = "/{postId}/comments/{commentId}")
    public String edit(
            @PathVariable(name = "postId") Long postId,
            @PathVariable(name = "commentId") Long commentId,
            @RequestParam(value = "text", defaultValue = "") String text) {
        CommentDto savedComment = commentService.patch(commentId, text);
        return "redirect:/posts/" + savedComment.getPostId().toString();
    }

    @PostMapping(value = "/{postId}/comments/{commentId}/delete")
    public String delete(
            @PathVariable(name = "postId") Long postId,
            @PathVariable(name = "commentId") Long commentId) {
        commentService.delete(commentId);
        return "redirect:/posts/" + postId.toString();
    }
}