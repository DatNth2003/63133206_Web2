package com.ntd63133206.bookbuddy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ntd63133206.bookbuddy.model.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {
	Tag findByName(String name);
    Tag save(Tag tag);

}