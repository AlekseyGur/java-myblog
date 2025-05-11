package ru.alexgur.blog.tag.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.alexgur.blog.tag.dto.PairIdsDto;
import ru.alexgur.blog.tag.dto.TagDto;
import ru.alexgur.blog.tag.interfaces.TagService;
import ru.alexgur.blog.tag.interfaces.TagRepository;
import ru.alexgur.blog.tag.mapper.TagMapper;
import ru.alexgur.blog.tag.model.Tag;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;

    @Override
    public List<TagDto> add(Long postId, List<String> tags) {
        Set<String> uniqueNames = new HashSet<>(tags);

        Set<String> savedNames = tagRepository.getByName(tags).stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        uniqueNames.removeAll(savedNames);

        tagRepository.add(uniqueNames.stream().toList());

        List<Tag> savedTags = tagRepository.getByName(tags);
        List<Long> savedTagsIds = savedTags.stream().map(Tag::getId).toList();
        tagRepository.addTagsIdsToPost(postId, savedTagsIds);

        return TagMapper.toDto(savedTags);
    }

    @Override
    public List<TagDto> getById(List<Long> tagsIds) {
        return TagMapper.toDto(tagRepository.getById(tagsIds));
    }

    @Override
    public List<TagDto> getByName(List<String> tags) {
        return TagMapper.toDto(tagRepository.getByName(tags));
    }

    @Override
    public List<TagDto> getByPostId(Long postId) {
        return TagMapper.toDto(tagRepository.getByPostId(postId));
    }

    @Override
    public void deleteByPostId(Long postId) {
        tagRepository.deleteByPostId(postId);
    }

    @Override
    public Map<Long, List<TagDto>> getByPostId(List<Long> postIds) {
        HashMap<Long, List<TagDto>> res = new HashMap<>();

        List<PairIdsDto> postToTag = tagRepository.getPostIdTagIdPair(postIds);
        List<Long> tagsIds = postToTag.stream().map(PairIdsDto::getLast).toList();
        if (tagsIds.isEmpty()) {
            return res;
        }

        Map<Long, Tag> tags = tagRepository.getById(tagsIds).stream()
                .collect(Collectors.toMap(Tag::getId, Function.identity()));

        for (PairIdsDto row : postToTag) {
            Long postId = row.getFirst();
            Long tagId = row.getLast();
            if (!tags.containsKey(tagId)) {
                continue;
            }
            TagDto tag = TagMapper.toDto(tags.get(tagId));
            res.computeIfAbsent(postId, k -> new ArrayList<>()).add(tag);
        }
        return res;
    }
}