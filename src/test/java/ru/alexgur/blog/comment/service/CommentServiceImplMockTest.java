package ru.alexgur.blog.comment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.alexgur.blog.TestWebConfiguration;
import ru.alexgur.blog.comment.dto.CommentDto;
import ru.alexgur.blog.comment.interfaces.CommentRepository;
import ru.alexgur.blog.comment.model.Comment;
import ru.alexgur.blog.post.interfaces.PostService;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplMockTest extends TestWebConfiguration {

    @InjectMocks
    private CommentServiceImpl commentServiceImpl;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostService postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void add() {
        Long postId = 1L;
        String text = "Текст комментария";

        Comment savedComment = new Comment();
        savedComment.setId(1L);
        savedComment.setPostId(postId);
        savedComment.setText(text);

        when(postService.checkIdExist(postId)).thenReturn(true);
        when(commentRepository.add(any(Comment.class))).thenReturn(Optional.of(savedComment));

        CommentDto result = commentServiceImpl.add(postId, text);

        assertNotNull(result);
        assertEquals(savedComment.getId(), result.getId());
        assertEquals(savedComment.getPostId(), result.getPostId());
        assertEquals(savedComment.getText(), result.getText());

        verify(postService, times(1)).checkIdExist(postId);
        verify(commentRepository, times(1)).add(any(Comment.class));
    }

    @Test
    void addWrotngPostId() {
        Long postId = 1L;
        String text = "Текст комментария";

        when(postService.checkIdExist(postId)).thenReturn(false);

        assertThrows(Exception.class, () -> {
            commentServiceImpl.add(postId, text);
        });

        verify(postService, times(1)).checkIdExist(postId);
    }

    @Test
    void patchWrotngCommentId() {
        Long commentId = 9999999L;
        String text = "Текст комментария";

        when(commentRepository.checkIdExist(commentId)).thenReturn(false);

        assertThrows(Exception.class, () -> {
            commentServiceImpl.patch(commentId, text);
        });

        verify(commentRepository, times(1)).checkIdExist(commentId);
    }
}
