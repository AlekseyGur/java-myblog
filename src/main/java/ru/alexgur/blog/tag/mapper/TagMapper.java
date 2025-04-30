package ru.alexgur.blog.tag.mapper;

import java.util.List;

import lombok.experimental.UtilityClass;
import ru.alexgur.blog.tag.dto.TagDto;
import ru.alexgur.blog.tag.model.Tag;

@UtilityClass
public class TagMapper {
    public static TagDto toDto(Tag tag) {
        TagDto tagDto = new TagDto();
        tagDto.setId(tag.getId());
        tagDto.setName(tag.getName());
        return tagDto;
    }

    public static Tag toTag(TagDto tagDto) {
        Tag tag = new Tag();
        tag.setId(tagDto.getId());
        tag.setName(tagDto.getName());
        return tag;
    }

    public static List<Tag> toTag(List<TagDto> tagsDto) {
        return tagsDto.stream().map(TagMapper::toTag).toList();
    }

    public static List<TagDto> toDto(List<Tag> tags) {
        return tags.stream().map(TagMapper::toDto).toList();
    }
}