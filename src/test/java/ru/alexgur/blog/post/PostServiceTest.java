package ru.alexgur.blog.post;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import ru.alexgur.blog.WebConfigurationTest;
import ru.alexgur.blog.post.interfaces.PostService;
import ru.alexgur.blog.post.repository.PostSqlRepository;

@SpringJUnitConfig(classes = { WebConfigurationTest.class })
public class PostServiceTest {
    @Autowired
    private PostService postService;

    @Mock
    private PostSqlRepository postRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckIdExist_whenIdExists() {
        when(postRepository.checkIdExist(anyLong()))
                .thenReturn(true);

        boolean result = postService.checkIdExist(1L);

        assert result == true;
    }

    @Test
    void testCheckIdExist_whenIdDoesNotExist() {
        when(postRepository.checkIdExist(anyLong()))
                .thenReturn(false);

        boolean result = postService.checkIdExist(1L);

        assert result == false;
    }

    @Test
    void testCheckIdExist() {
        postService.checkIdExist(1L);
    }

    @Test
    void testAdd() {
        postService.add(null, null, null, null);
    }

    @Test
    void testDelete() {

    }

    @Test
    void testGet() {

    }

    @Test
    void testGetAll() {

    }

    @Test
    void testLike() {

    }

    @Test
    void testPatch() {

    }
}
