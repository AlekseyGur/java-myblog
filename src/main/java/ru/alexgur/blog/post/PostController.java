package ru.alexgur.blog.post;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;

import lombok.RequiredArgsConstructor;
import ru.alexgur.blog.post.dto.PostDto;
import ru.alexgur.blog.post.interfaces.PostService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String get(@PathVariable Long id, Model model) {
        PostDto post = postService.get(id);
        model.addAttribute("post", post);
        return "post";
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
            Model model) {
        List<PostDto> posts = postService.getAll(search, pageSize, pageNumber);
        model.addAttribute("posts", posts);
        return "posts";
    }

    @GetMapping("/add")
    public String addForm() {
        return "add-post";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        PostDto post = postService.get(id);

        if (post == null) {
            return "redirect:/add";
        }

        model.addAttribute("post", post);
        return "add-post";
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public String addPost(
            @RequestParam("title") String title,
            @RequestParam("text") String text,
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "tags", required = false, defaultValue = "") String tags) {
        PostDto savedPost = postService.add(title, text, tags, image);

        return "redirect:" + savedPost.getUrl();
    }

    @PostMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public String editPost(@PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("text") String text,
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "tags", required = false, defaultValue = "") String tags) {
        PostDto savedPost = postService.patch(id, title, text, tags, image);

        return "redirect:" + savedPost.getUrl();
    }

    @PutMapping("/{id}/like")
    @ResponseStatus(HttpStatus.OK)
    public void addLike(
            @PathVariable Long id,
            @RequestParam(name = "like") boolean isLike) {
        postService.like(id, isLike);
    }

    @PostMapping(value = "/{postId}/delete", params = "_method=delete")
    @ResponseStatus(HttpStatus.OK)
    public String delete(@PathVariable Long postId) {
        postService.delete(postId);
        return "redirect:/posts";
    }
}
