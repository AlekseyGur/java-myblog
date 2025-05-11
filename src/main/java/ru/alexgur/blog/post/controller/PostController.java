package ru.alexgur.blog.post.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import ru.alexgur.blog.post.dto.Paging;
import ru.alexgur.blog.post.dto.PostDto;
import ru.alexgur.blog.post.interfaces.PostService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
@Validated
public class PostController {
    private final PostService postService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String get(@PathVariable @Positive Long id, Model model) {
        PostDto post = postService.get(id);
        model.addAttribute("post", post);
        return "post";
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String getAll(
            @RequestParam(value = "tag", required = false) String tag,
                    @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "pageSize", defaultValue = "10") @Positive Integer pageSize,
                    @RequestParam(value = "pageNumber", defaultValue = "1") @Positive Integer pageNumber,
            Model model) {

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<PostDto> posts;

        if (search != null && !search.isBlank()) {
            posts = postService.find(search, pageable);
        } else if (tag != null && !tag.isBlank()) {
            posts = postService.getByTagName(tag, pageable);
        } else {
            posts = postService.getAll(pageable);
        }

        model.addAttribute("posts", posts);
        model.addAttribute("paging", new Paging(posts));
        model.addAttribute("search", search);

        return "posts";
    }


    @GetMapping("/add")
    @ResponseStatus(HttpStatus.OK)
    public String addForm() {
        return "add-post";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable(name = "id") Long id, Model model) {
        PostDto post = postService.get(id);

        if (post == null) {
            return "redirect:/posts/add";
        }

        model.addAttribute("post", post);
        return "add-post";
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String addPost(
            @RequestParam(value = "title") String title,
            @RequestParam(value = "text") String text,
            @RequestParam(value = "image", required = false) MultipartFile image,
                    @RequestParam(value = "tags", required = false, defaultValue = "") String tags) {
        PostDto savedPost = postService.add(title, text, tags, image);

        return "redirect:" + savedPost.getUrl();
    }

    @PostMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String editPost(@PathVariable(name = "id") Long id,
            @RequestParam(value = "title") String title,
                    @RequestParam(value = "text") String text,
            @RequestParam(value = "image") MultipartFile image,
            @RequestParam(value = "tags", required = false, defaultValue = "") String tags) {
        PostDto savedPost = postService.patch(id, title, text, tags, image);

        return "redirect:/posts/" + savedPost.getId();
    }

    @PostMapping("/{postId}/like")
    public String addLike(@PathVariable(name = "postId") Long postId,
            @RequestParam(name = "like") boolean isLike) {
        postService.like(postId, isLike);
        return "redirect:/posts/" + postId.toString();
    }

    @PostMapping(value = "/{postId}/delete")
    public String deletePost(@PathVariable(name = "postId") Long postId) {
        postService.delete(postId);
        return "redirect:/posts";
    }
}
