package ru.alexgur.blog.post.controller;

import ru.alexgur.blog.configuration.TestDataSourceConfiguration;
import ru.alexgur.blog.configuration.TestWebConfiguration;
import ru.alexgur.blog.post.interfaces.PostRepository;
import ru.alexgur.blog.post.model.Post;
import javassist.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;
import static org.hamcrest.Matchers.*;

@SpringJUnitConfig(classes = { TestDataSourceConfiguration.class,
        TestWebConfiguration.class })
@ActiveProfiles("test")
@WebAppConfiguration
@TestPropertySource(locations = "classpath:test-application.properties")
class PostControllerTest {

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

    // @Test
    // void getStartPage() throws Exception {
    // mockMvc.perform(get("/"))
    // .andExpect(status().is3xxRedirection())
    // .andExpect(redirectedUrl("feed"))
    // .andExpect(view().name("redirect:feed"));
    // }

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

    // @Test
    // @Sql(scripts = "classpath:test-data/add-three-posts.sql")
    // void getPost() throws Exception {
    // var posts = getAllPost();
    // for (var entry : posts.entrySet()) {
    // mockMvc.perform(get("/post/" + entry.getKey()))
    // .andExpect(status().isOk())
    // .andExpect(view().name("post"))
    // .andExpect(model().attributeExists("postDto", "newPostDto"))
    // .andExpect(xpath("//span['" + entry.getKey() + "']").exists());
    // }
    // }

    // @Test
    // @Sql(scripts = "classpath:test-data/add-three-posts.sql")
    // void deletePost() throws Exception {
    // Post post = getFirstPost();
    // long postId = post.getId();
    // String postName = post.getTitle();

    // mockMvc.perform(get("/feed"))
    // .andExpect(status().isOk())
    // .andExpect(content().contentType("text/html;charset=UTF-8"))
    // .andExpect(view().name("feed"))
    // .andExpect(model().attributeExists("postPage", "pageSize", "isFiltered"))
    // .andExpect(xpath("//a[text()='" + postName + "']/@href").exists());

    // mockMvc.perform(post("/post/" + postId).param("_method", "delete"))
    // .andExpect(redirectedUrl("/feed"))
    // .andExpect(view().name("redirect:/feed"));

    // mockMvc.perform(get("/feed"))
    // .andExpect(status().isOk())
    // .andExpect(content().contentType("text/html;charset=UTF-8"))
    // .andExpect(view().name("feed"))
    // .andExpect(model().attributeExists("postPage", "pageSize", "isFiltered"))
    // .andExpect(xpath("//a[text()='" + postName + "']/@href").doesNotExist());
    // }

    // @Test
    // @Sql(scripts = "classpath:test-data/add-three-posts.sql")
    // void addLike() throws Exception {
    // Post post = getFirstPost();
    // long postId = post.getId();
    // long likes = post.getLikes();
    // mockMvc.perform(post("/post/like/" + postId))
    // .andExpect(redirectedUrl("/post/" + postId))
    // .andExpect(view().name("redirect:/post/" + postId));
    // mockMvc.perform(get("/post/" + postId))
    // .andExpect(xpath("//span[@id='likes']").string(String.valueOf(++likes)));
    // }

    // private Post getFirstPost() throws NotFoundException {
    // return postRepository.getAll(0, 10)
    // .stream().findFirst()
    // .orElseThrow(() -> new NotFoundException("пост не найден"));
    // }

    // private Map<Long, String> getAllPost() {
    // return postRepository.getAll(0, 10)
    // .stream().collect(Collectors.toMap(Post::getId, Post::getText));
    // }
}