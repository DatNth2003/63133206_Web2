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

    public Tag getTagById(Long id) {
        Optional<Tag> optionalTag = tagRepository.findById(id);
        return optionalTag.orElse(null);
    }

    public Tag createTag(Tag tag) {
        return tagRepository.save(tag);
    }

    public Tag updateTag(Long id, Tag updatedTag) {
        Tag existingTag = getTagById(id);
        if (existingTag != null) {
            existingTag.setName(updatedTag.getName());
            // Cập nhật các trường khác của tag nếu cần
            return tagRepository.save(existingTag);
        }
        return null;
    }

    public void deleteTag(Long id) {
        tagRepository.deleteById(id);
    }
}