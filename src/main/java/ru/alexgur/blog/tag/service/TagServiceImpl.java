package ru.alexgur.blog.tag.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.alexgur.blog.tag.dto.TagDto;
import ru.alexgur.blog.tag.interfaces.TagService;
import ru.alexgur.blog.tag.interfaces.TagRepository;
import ru.alexgur.blog.tag.mapper.TagMapper;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagReposiitory;

    @Override
    public List<TagDto> add(Long postId, List<String> tags) {
        Set<String> uniqueNames = new HashSet<>(tags);

        Set<String> savedNames = tagReposiitory.getByName(tags).stream()
                .map(x -> x.getName())
                .collect(Collectors.toSet());

        uniqueNames.removeAll(savedNames);

        tagReposiitory.add(uniqueNames.stream().toList());

        return TagMapper.toDto(tagReposiitory.getByName(tags));
    }

    @Override
    public List<TagDto> getByName(List<String> tags) {
        return TagMapper.toDto(tagReposiitory.getByName(tags));
    }

    @Override
    public List<TagDto> getByPostId(Long postId) {
        return TagMapper.toDto(tagReposiitory.getByPostId(postId));
    }

}