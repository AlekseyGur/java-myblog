package ru.alexgur.blog.comment.controller;

import ru.alexgur.blog.TestWebConfiguration;
import ru.alexgur.blog.post.interfaces.PostRepository;
import ru.alexgur.blog.system.exception.NotFoundException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.sql.PreparedStatement;
import java.sql.Statement;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.hamcrest.Matchers.*;

class CommentControllerTest extends TestWebConfiguration {

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
    @Sql(scripts = "classpath:test-data/add-post.sql")
    void add() throws Exception {
        long postId = getFirstPostId();
        String newComment = "new test comment";
        mockMvc.perform(post("/posts/" + postId + "/comments")
                .param("_method", "add")
                .param("text", newComment))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + postId))
                .andExpect(view().name("redirect:/posts/" + postId));

        mockMvc.perform(get("/posts/" + postId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(newComment)));
    }

    @Test
    @Sql(scripts = "classpath:test-data/add-post.sql")
    void edit() throws Exception {
        long postId = getFirstPostId();
        String commentText = "New comment " + (int) (Math.random() * 1000);
        long commentId = addAndGetId(commentText, postId);

        mockMvc.perform(get("/posts/" + postId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(commentText)));

        commentText = "Changed text " + (int) (Math.random() * 1000);

        MvcResult result = mockMvc.perform(post("/posts/" + postId + "/comments/" + commentId)
                .param("text", commentText))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + postId))
                .andReturn();

        String redirectUrl = result.getResponse().getHeader("Location");

        mockMvc.perform(get(redirectUrl))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(commentText)));
    }

    @Test
    @Sql(scripts = "classpath:test-data/add-post.sql")
    void editEmptyTextShouldNotChangeComment() throws Exception {
            long postId = getFirstPostId();

            String commentText = "New comment " + (int) (Math.random() * 1000);
            String emptyCommentText = "";

            long commentId = addAndGetId(commentText, postId);

            mockMvc.perform(get("/posts/" + postId))
                            .andExpect(status().isOk())
                            .andExpect(content().string(containsString(commentText)));

            MvcResult result = mockMvc.perform(post("/posts/" + postId + "/comments/" + commentId)
                            .param("text", emptyCommentText))
                            .andExpect(status().is3xxRedirection())
                            .andExpect(redirectedUrl("/posts/" + postId))
                            .andReturn();

            String redirectUrl = result.getResponse().getHeader("Location");

            mockMvc.perform(get(redirectUrl))
                            .andExpect(status().isOk())
                            .andExpect(content().string(containsString(commentText)));
    }

    @Test
    @Sql(scripts = "classpath:test-data/add-post.sql")
    void editWrongCommentShouldThrowError() throws Exception {
            long postId = 999997L;
            long commentId = 999997L;

            mockMvc.perform(post("/posts/" + postId + "/comments/" + commentId)
                            .param("text", "New Text Comment"))
                            .andExpect(status().is4xxClientError());
    }

    @Test
    @Sql(scripts = "classpath:test-data/add-post.sql")
    void delete() throws Exception {
        long postId = getFirstPostId();
        String commentText = "comment to delete " + (int) (Math.random() * 1000);
        long commentId = addAndGetId(commentText, postId);

        mockMvc.perform(get("/posts/" + postId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(commentText)));

        String url = "/posts/" + postId + "/comments/" + commentId + "/delete";
        mockMvc.perform(post(url).param("_method", "delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + postId))
                .andExpect(view().name("redirect:/posts/" + postId));

        mockMvc.perform(get("/posts/" + postId))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString(commentText))));
    }

    private long getFirstPostId() throws NotFoundException {
        return postRepository.getAll(Pageable.ofSize(10))
                .stream().findFirst()
                .orElseThrow(() -> new NotFoundException("пост не найден")).getId();
    }

    private long addAndGetId(String commentText, long postId) {
        String query = """
                INSERT INTO comments(text,post_id)
                VALUES (?,?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, commentText);
            ps.setLong(2, postId);
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new RuntimeException("Пост не добавлен");
        }
        return key.longValue();
    }
}