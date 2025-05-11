package ru.alexgur.blog.post.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import ru.alexgur.blog.post.interfaces.PostImageService;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${upload.images.base-url}")
@Validated
public class PostImageController {
    private final PostImageService postImageService;

    @GetMapping("/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<StreamingResponseBody> load(@PathVariable(name = "postId") @Positive Long postId) {
        return postImageService.load(postId);
    }
}
