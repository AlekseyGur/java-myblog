package ru.alexgur.blog.comment;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.alexgur.blog.comment.dto.CommentDto;
import ru.alexgur.blog.comment.interfaces.CommentService;
import ru.alexgur.blog.comment.utils.CommentValidate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getByUserId(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId) {
        return commentService.getByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public CommentDto add(
            @RequestBody CommentDto comment,
            @RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId) {
        comment.setOwner(userId);
        CommentValidate.comment(comment);
        return commentService.add(comment);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto get(@PathVariable Long id) {
        return commentService.get(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Long id) {
        commentService.delete(id);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto patch(
            @PathVariable Long commentId,
            @RequestBody CommentDto comment,
            @RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId) {
        comment.setId(commentId);
        comment.setOwner(userId);
        return commentService.patch(comment);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> search(@RequestParam String text) {
        return commentService.find(text);
    }
}