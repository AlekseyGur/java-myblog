package ru.alexgur.blog.post;

import java.util.List;

import org.springframework.http.HttpStatus;
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
    public List<PostDto> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        return postService.getAll(search, pageSize, pageNumber);
    }

    @GetMapping("/add")
    public String addForm() {
        return "post";
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public String add(
            @RequestParam("title") String title,
            @RequestParam("text") String text,
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "tags", required = false, defaultValue = "") String tags) {
        PostDto savedPost = postService.add(title, text, tags, image);

        return "redirect:" + savedPost.getUrl();
    }

    @PutMapping("/{id}/like")
    @ResponseStatus(HttpStatus.OK)
    public void addLike(
            @PathVariable Long id,
            @RequestParam(name = "like") boolean isLike) {
        postService.like(id, isLike);
    }

    // @ ----------------------------------------------------------------------

    // // @DeleteMapping("/{id}")
    // @PostMapping(value = "/{id}", params = "_method=delete")
    // @ResponseStatus(HttpStatus.OK)
    // public String delete(@PathVariable Long id) {
    // postService.delete(id);
    // return "redirect:/posts";
    // }

    // @PatchMapping("/{postId}")
    // @ResponseStatus(HttpStatus.OK)
    // public PostDto patch(
    // @PathVariable Long postId,
    // @RequestBody PostDto post,
    // @RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId) {
    // post.setId(postId);
    // post.setOwner(userId);
    // return postService.patch(post);
    // }

    // @GetMapping("/search")
    // @ResponseStatus(HttpStatus.OK)
    // public List<PostDto> search(@RequestParam String text) {
    // return postService.find(text);
    // }
}
