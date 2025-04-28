package ru.alexgur.blog.tag.utils;

import jakarta.validation.Valid;
import lombok.experimental.UtilityClass;
import ru.alexgur.blog.system.exception.ValidationException;
import ru.alexgur.blog.tag.dto.TagDto;

@UtilityClass
public class TagValidate {
    public static void tag(@Valid TagDto tag) {
        if (tag.getName() != null && tag.getName().isBlank()) {
            throw new ValidationException("Укажите название");
        }
    }

    public static void tagDto(@Valid TagDto tag) {
        if (tag.getName() == null || tag.getName().isBlank()) {
            throw new ValidationException("Укажите название");
        }
    }
}
