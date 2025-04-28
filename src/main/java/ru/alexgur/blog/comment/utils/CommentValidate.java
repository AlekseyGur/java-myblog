package ru.alexgur.blog.comment.utils;

import jakarta.validation.Valid;
import lombok.experimental.UtilityClass;
import ru.alexgur.blog.system.exception.ValidationException;
import ru.alexgur.blog.comment.dto.CommentDto;

@UtilityClass
public class CommentValidate {
    public static void comment(@Valid CommentDto comment) {
        if (comment.getOwner() == null) {
            throw new ValidationException("Укажите владельца");
        }

        if (comment.getName() != null && comment.getName().isBlank()) {
            throw new ValidationException("Укажите название");
        }

        if (comment.getDescription() == null || comment.getDescription().isBlank()) {
            throw new ValidationException("Задайте описание");
        }

        if (comment.getAvailable() == null) {
            throw new ValidationException("Укажите статус о том, доступна или нет вещь для аренды");
        }
    }

    public static void commentDto(@Valid CommentDto comment) {
        if (comment.getName() == null || comment.getName().isBlank()) {
            throw new ValidationException("Укажите название");
        }

        if (comment.getDescription() == null || comment.getDescription().isBlank()) {
            throw new ValidationException("задайте описание");
        }
    }
}
