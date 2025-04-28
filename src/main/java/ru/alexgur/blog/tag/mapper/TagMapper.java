package ru.alexgur.blog.tag.mapper;

import java.util.List;

import lombok.experimental.UtilityClass;
import ru.alexgur.blog.tag.dto.TagDto;
import ru.alexgur.blog.tag.model.Tag;

@UtilityClass
public class TagMapper {
    public static TagDto tagToDto(Tag tag) {
        TagDto tagDto = new TagDto();
        tagDto.setId(tag.getId());
        tagDto.setName(tag.getName());
        return tagDto;
    }

    public static Tag dtoToTag(TagDto tagDto) {
        Tag tag = new Tag();
        tag.setId(tagDto.getId());
        tag.setName(tagDto.getName());
        return tag;
    }

    public static List<Tag> dtoToTag(List<TagDto> tagsDto) {
        return tagsDto.stream().map(TagMapper::dtoToTag).toList();
    }

    public static List<TagDto> tagToDto(List<Tag> tags) {
        return tags.stream().map(TagMapper::tagToDto).toList();
    }
}