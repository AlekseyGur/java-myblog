package ru.alexgur.blog.tag.dto;

import lombok.Getter;
import lombok.Setter;
import ru.alexgur.blog.tag.model.Tag;

@Getter
@Setter
public class PairIdsDto {
    private Long first;
    private Long last;
}