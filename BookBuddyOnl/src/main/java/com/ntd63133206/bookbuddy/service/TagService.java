package com.ntd63133206.bookbuddy.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ntd63133206.bookbuddy.model.Tag;
import com.ntd63133206.bookbuddy.repository.TagRepository;

@Service
public class TagService {
	@Autowired
    private TagRepository tagRepository;

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    public void addTag(Tag tag) {
        tagRepository.save(tag);
    }

    public Tag getTagById(Long id) {
        Optional<Tag> result = tagRepository.findById(id);
        return result.orElse(null);
    }
    public Tag findByName(String name) {
        return tagRepository.findByName(name);
    }

    public void save(Tag tag) {
        tagRepository.save(tag);
    }
    public void updateTag(Long id, Tag tag) {
        Tag existingTag = getTagById(id);
        if (existingTag != null) {
            existingTag.setName(tag.getName());
            tagRepository.save(existingTag);
        }
    }

    public void deleteTag(Long id) {
        tagRepository.deleteById(id);
    }
}