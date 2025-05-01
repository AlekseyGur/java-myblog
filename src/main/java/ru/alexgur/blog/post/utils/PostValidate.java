package ru.alexgur.blog.post.utils;

// import jakarta.validation.Valid;
import lombok.experimental.UtilityClass;
import ru.alexgur.blog.system.exception.ValidationException;
import ru.alexgur.blog.post.dto.PostDto;

@UtilityClass
public class PostValidate {
    // public static void post(@Valid PostDto post) {
    public static void post(PostDto post) {
        if (post.getTitle() != null && post.getTitle().isBlank()) {
            throw new ValidationException("Укажите название");
        }

        if (post.getText() == null || post.getText().isBlank()) {
            throw new ValidationException("Напишите содержание");
        }
    }

    // public static void postDto(@Valid PostDto post) {
    public static void postDto(PostDto post) {
        if (post.getTitle() == null || post.getTitle().isBlank()) {
            throw new ValidationException("Укажите название");
        }

        if (post.getText() == null || post.getText().isBlank()) {
            throw new ValidationException("Напишите содержание");
        }
    }
}
