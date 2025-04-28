package ru.alexgur.blog.tag;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.alexgur.blog.tag.dto.TagDto;
import ru.alexgur.blog.tag.interfaces.TagService;
import ru.alexgur.blog.tag.interfaces.TagStorage;
import ru.alexgur.blog.tag.mapper.TagMapper;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagStorage tagStorage;

    @Override
    public List<TagDto> add(Long postId, List<String> tags) {
        Set<String> uniqueNames = new HashSet<>(tags);

        Set<String> savedNames = tagStorage.getByName(tags).stream()
                .map(x -> x.getName())
                .collect(Collectors.toSet());

        uniqueNames.removeAll(savedNames);

        tagStorage.add(uniqueNames.stream().toList());

        return TagMapper.tagToDto(tagStorage.getByName(tags));
    }

    @Override
    public List<TagDto> getByName(List<String> tags) {
        return TagMapper.tagToDto(tagStorage.getByName(tags));
    }

    @Override
    public List<TagDto> getByPostId(Long postId) {
        return TagMapper.tagToDto(tagStorage.getByPostId(postId));
    }

}