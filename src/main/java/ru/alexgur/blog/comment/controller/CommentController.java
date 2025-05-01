package ru.alexgur.blog.comment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import lombok.RequiredArgsConstructor;
import ru.alexgur.blog.comment.dto.CommentDto;
import ru.alexgur.blog.comment.interfaces.CommentService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class CommentController {
    private final CommentService commentService;

    @PostMapping(value = "/{postId}/comments", params = "_method=add")
    public String addComment(
            @PathVariable(name = "postId") Long postId,
            @RequestParam(value = "text") String text) {
        commentService.add(postId, text);
        return "redirect:/posts/" + postId.toString();
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String editComment(
            @PathVariable(name = "id") Long id,
            @RequestBody CommentDto commentDto) {
        CommentDto savedComment = commentService.patch(commentDto);

        return "redirect:/posts/" + savedComment.getPostId().toString();
    }

    @PostMapping(value = "/{postId}/comments/{commentId}/delete", params = "_method=delete")
    public String deleteComment(
            @PathVariable(name = "postId") Long postId,
            @PathVariable(name = "commentId") Long commentId) {
        commentService.delete(commentId);
        return "redirect:/posts/" + postId.toString();
    }
}