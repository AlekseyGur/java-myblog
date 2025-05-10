package ru.alexgur.blog.post.controller;

import ru.alexgur.blog.post.interfaces.PostRepository;
import ru.alexgur.blog.post.model.Post;
import ru.alexgur.blog.system.exception.NotFoundException;
import ru.alexgur.blog.TestWebConfiguration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.hamcrest.Matchers.*;

class PostControllerTest extends TestWebConfiguration {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private PostRepository postRepository;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @AfterEach
    void cleanUp() {
        jdbc.execute("DELETE FROM tags_post");
        jdbc.execute("DELETE FROM comments");
        jdbc.execute("DELETE FROM tags");
        jdbc.execute("DELETE FROM posts");
    }

    @Test
    void getStartPage() throws Exception {
        mockMvc.perform(get("/posts/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-post"));
    }

    @Test
    @Sql(scripts = "classpath:test-data/add-three-posts.sql")
    void getFeed() throws Exception {
        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("posts"))
                // .andExpect(model().attributeExists("postPage", "pageSize", "isFiltered"))
                .andExpect(content().string(containsString("Первая")))
                .andExpect(content().string(containsString("Вторая")))
                .andExpect(content().string(containsString("Третья")));
    }

    @Test
    @Sql(scripts = "classpath:test-data/add-three-posts.sql")
    void getPost() throws Exception {
        for (Post post : getAllPost()) {
            mockMvc.perform(get("/posts/" + post.getId()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("post"))
                    .andExpect(content().string(containsString(post.getTitle())));
        }
    }

    @Test
    @Sql(scripts = "classpath:test-data/add-three-posts.sql")
    void deletePost() throws Exception {
        Post post = getFirstPost();
        long postId = post.getId();

        mockMvc.perform(get("/posts/" + postId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("post"))
                .andExpect(model().attributeExists("post"))
                .andExpect(content().string(containsString(post.getTitle())));

        mockMvc.perform(post("/posts/" + postId + "/delete").param("_method", "delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"))
                .andExpect(view().name("redirect:/posts"));

        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("posts"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(content().string(not(containsString(post.getTitle()))));
    }

    @Test
    @Sql(scripts = "classpath:test-data/add-three-posts.sql")
    void addLike() throws Exception {
        Post post = getFirstPost();
        long postId = post.getId();
        long likes = post.getLikes();

        mockMvc.perform(post("/posts/" + postId + "/like").param("like", "true"))
                .andExpect(redirectedUrl("/posts/" + postId))
                .andExpect(view().name("redirect:/posts/" + postId));

        mockMvc.perform(get("/posts/" + postId))
                .andExpect(content().string(containsString(String.valueOf(++likes))));
    }

    private Post getFirstPost() throws NotFoundException {
        return postRepository.getAll(Pageable.ofSize(1))
                .stream().findFirst()
                .orElseThrow(() -> new NotFoundException("пост не найден"));
    }

    private Page<Post> getAllPost() {
        return postRepository.getAll(Pageable.ofSize(10));
    }
}