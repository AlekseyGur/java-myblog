package ru.alexgur.blog.post.controller;

import ru.alexgur.blog.post.interfaces.PostRepository;
import ru.alexgur.blog.post.model.Post;
import ru.alexgur.blog.system.exception.NotFoundException;
import ru.alexgur.blog.TestWebConfiguration;
import ru.alexgur.blog.UtilsTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PostControllerTest extends TestWebConfiguration {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private PostRepository postRepository;
    private MockMvc mockMvc;

    @Value("${classpath:test-data/image.jpg}")
    String imageJpg;

    @Value("${classpath:test-data/image.png}")
    String imagePng;

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
    void getEditPage() throws Exception {
        MvcResult result = addPostImpl();
        String redirectUrl = result.getResponse().getHeader("Location");
        String[] parts = redirectUrl.split("/");
        Long postId = Long.parseLong(parts[parts.length - 1]);

        mockMvc.perform(get("/posts/{postId}/edit", 9999L))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", startsWith("/posts/add")));

        mockMvc.perform(get("/posts/{postId}/edit", postId))
                .andExpect(status().isOk())
                .andExpect(view().name("add-post"));
    }

    @Test
    void addPost() throws Exception {
        String title = "Заголовок";
        String text = "Текст поста";
        String tag1 = "тег1";
        String tag2 = "тег2";
        String tags = tag1 + ", " + tag2;

        Resource resource = new ClassPathResource(imageJpg);
        MockMultipartFile image = new MockMultipartFile(
                "image", // имя параметра в форме
                resource.getFilename(),
                MediaType.IMAGE_JPEG_VALUE,
                resource.getInputStream());

        MvcResult result = mockMvc.perform(multipart("/posts")
                .file(image)
                .param("title", title)
                .param("text", text)
                .param("tags", tags)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", startsWith("/posts/")))
                .andReturn();

        String redirectUrl = result.getResponse().getHeader("Location");

        mockMvc.perform(get(redirectUrl))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(title)))
                .andExpect(content().string(containsString(text)))
                .andExpect(content().string(containsString(tag1)))
                .andExpect(content().string(containsString(tag2)));

    }

    @Test
    void editPost() throws Exception {
        MvcResult result = addPostImpl();

        String redirectUrl = result.getResponse().getHeader("Location");
        String[] parts = redirectUrl.split("/");
        Long postId = Long.parseLong(parts[parts.length - 1]);

        String title = "Заголовок";
        String text = "Текст поста";
        String tag1 = "java";
        String tag2 = "spring";
        String tags = tag1 + ", " + tag2;

        Resource resource = new ClassPathResource(imageJpg);
        MockMultipartFile image = new MockMultipartFile(
                "image", // имя параметра в форме
                resource.getFilename(),
                MediaType.IMAGE_JPEG_VALUE,
                resource.getInputStream());

        result = mockMvc.perform(multipart("/posts/" + postId)
                .file(image)
                .param("title", title)
                .param("text", text)
                .param("tags", tags)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + postId))
                .andReturn();

        mockMvc.perform(get(redirectUrl))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(title)))
                .andExpect(content().string(containsString(text)))
                .andExpect(content().string(containsString(tag1)))
                .andExpect(content().string(containsString(tag2)));
    }

    @Test
    void search() throws Exception {
        String title = "Заголовок для поиска1";
        String text = "Текст поста для поиска2";
        String tag1 = "тег1";
        String tag2 = "тег2";
        String tags = tag1 + ", " + tag2;

        Resource resource = new ClassPathResource(imageJpg);
        MockMultipartFile image = new MockMultipartFile(
                "image", // имя параметра в форме
                resource.getFilename(),
                MediaType.IMAGE_JPEG_VALUE,
                resource.getInputStream());

        mockMvc.perform(multipart("/posts")
                .file(image)
                .param("title", title)
                .param("text", text)
                .param("tags", tags)
                .contentType(MediaType.MULTIPART_FORM_DATA));

        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(title)))
                .andExpect(content().string(containsString(text)))
                .andExpect(content().string(containsString(tag1)))
                .andExpect(content().string(containsString(tag2)));

        mockMvc.perform(get("/posts").param("search", "поиска1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(title)))
                .andExpect(content().string(containsString(text)))
                .andExpect(content().string(containsString(tag1)))
                .andExpect(content().string(containsString(tag2)));

        mockMvc.perform(get("/posts").param("search", "поиска2"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(title)))
                .andExpect(content().string(containsString(text)))
                .andExpect(content().string(containsString(tag1)))
                .andExpect(content().string(containsString(tag2)));

        mockMvc.perform(get("/posts").param("search", "поиска3"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString(title))))
                .andExpect(content().string(not(containsString(text))))
                .andExpect(content().string(not(containsString(tag1))))
                .andExpect(content().string(not(containsString(tag2))));
    }

    @Test
    void getPostsByTag() throws Exception {
        String title = "Заголовок для поиска1";
        String text = "Текст поста для поиска2";
        String tag1 = "тег1";
        String tag2 = "тег2";
        String tags = tag1 + ", " + tag2;

        Resource resource = new ClassPathResource(imageJpg);
        MockMultipartFile image = new MockMultipartFile(
                "image", // имя параметра в форме
                resource.getFilename(),
                MediaType.IMAGE_JPEG_VALUE,
                resource.getInputStream());

        mockMvc.perform(multipart("/posts")
                .file(image)
                .param("title", title)
                .param("text", text)
                .param("tags", tags)
                .contentType(MediaType.MULTIPART_FORM_DATA));

        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(title)))
                .andExpect(content().string(containsString(text)))
                .andExpect(content().string(containsString(tag1)))
                .andExpect(content().string(containsString(tag2)));

        mockMvc.perform(get("/posts").param("tag", tag1))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(title)))
                .andExpect(content().string(containsString(text)))
                .andExpect(content().string(containsString(tag1)))
                .andExpect(content().string(containsString(tag2)));

        mockMvc.perform(get("/posts").param("tag", tag2))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(title)))
                .andExpect(content().string(containsString(text)))
                .andExpect(content().string(containsString(tag1)))
                .andExpect(content().string(containsString(tag2)));

        mockMvc.perform(get("/posts").param("tag", "тег3"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString(title))))
                .andExpect(content().string(not(containsString(text))))
                .andExpect(content().string(not(containsString(tag1))))
                .andExpect(content().string(not(containsString(tag2))));
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

    private MvcResult addPostImpl() throws Exception {
        String title = "Заголовок " + UtilsTests.genString(15);
        String text = "Текст поста " + UtilsTests.genString(15);
        String tag1 = "тег" + UtilsTests.genString(5);
        String tag2 = "тег" + UtilsTests.genString(5);
        String tags = tag1 + ", " + tag2;

        Resource resource = new ClassPathResource(imageJpg);
        MockMultipartFile image = new MockMultipartFile(
                "image", // имя параметра в форме
                resource.getFilename(),
                MediaType.IMAGE_JPEG_VALUE,
                resource.getInputStream());

        return mockMvc.perform(multipart("/posts")
                .file(image)
                .param("title", title)
                .param("text", text)
                .param("tags", tags)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", startsWith("/posts/")))
                .andReturn();
    }
}