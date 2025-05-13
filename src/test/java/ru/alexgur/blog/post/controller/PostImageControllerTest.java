package ru.alexgur.blog.post.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import ru.alexgur.blog.TestWebConfiguration;
import ru.alexgur.blog.post.interfaces.PostImageService;

public class PostImageControllerTest extends TestWebConfiguration {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private PostImageService postImageService;

    private MockMvc mockMvc;

    @Value("${upload.images.base-url}")
    String baseImageUrl;

    @Value("${classpath:test-data/image.jpg}")
    String imageJpg;

    @Value("${classpath:test-data/image.png}")
    String imagePng;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @Sql(scripts = "classpath:test-data/add-three-posts.sql")
    void saveImage() throws Exception {
        Resource resource = new ClassPathResource(imageJpg);
        MockMultipartFile image = new MockMultipartFile(
                "file", // имя параметра в форме
                resource.getFilename(),
                MediaType.IMAGE_JPEG_VALUE,
                resource.getInputStream());

        byte[] originalBytes = resource.getInputStream().readAllBytes();

        postImageService.save(1L, image);
        ResponseEntity<StreamingResponseBody> picStream = postImageService.load(1L);

        assertNotNull(picStream);

        assertEquals(MediaType.IMAGE_JPEG_VALUE, picStream.getHeaders().getContentType().toString());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (OutputStream os = outputStream) {
            picStream.getBody().writeTo(os);
        }
        byte[] loadedBytes = outputStream.toByteArray();

        assertArrayEquals(originalBytes, loadedBytes);
        assertEquals(originalBytes.length, loadedBytes.length);
    }

    @Test
    @Sql(scripts = "classpath:test-data/add-three-posts.sql")
    void saveWrongFormatError() throws Exception {
        Resource resource = new ClassPathResource(imagePng);
        MockMultipartFile image = new MockMultipartFile(
                "file", // имя параметра в форме
                resource.getFilename(),
                MediaType.IMAGE_JPEG_VALUE,
                resource.getInputStream());

        assertThrows(Exception.class, () -> {
            postImageService.save(1L, image);
        });
    }

    @Test
    @Sql(scripts = "classpath:test-data/add-three-posts.sql")
    void loadImage() throws Exception {
        Resource resource = new ClassPathResource(imageJpg);
        MockMultipartFile image = new MockMultipartFile(
                "file", // имя параметра в форме
                resource.getFilename(),
                MediaType.IMAGE_JPEG_VALUE,
                resource.getInputStream());

        byte[] originalBytes = resource.getInputStream().readAllBytes();

        postImageService.save(1L, image);

        mockMvc.perform(get(baseImageUrl + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("image/*"))
                .andExpect(content().bytes(originalBytes));
    }
}
