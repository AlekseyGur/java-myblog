package ru.alexgur.blog.comment.utils;

// import jakarta.validation.Valid;
import lombok.experimental.UtilityClass;
import ru.alexgur.blog.system.exception.ValidationException;
import ru.alexgur.blog.comment.dto.CommentDto;

@UtilityClass
public class CommentValidate {
    // public static void comment(@Valid CommentDto comment) {
    public static void comment(CommentDto comment) {
        if (comment.getText().isBlank()) {
            throw new ValidationException("Укажите текст комментария");
        }

        if (comment.getPostId() == null) {
            throw new ValidationException("Укажите id публикации для комментирования");
        }
    }

    // public static void commentDto(@Valid CommentDto comment) {
    public static void commentDto(CommentDto comment) {
        if (comment.getText().isBlank()) {
            throw new ValidationException("Укажите текст комментария");
        }

        if (comment.getPostId() == null) {
            throw new ValidationException("Укажите id публикации для комментирования");
        }
    }
}
