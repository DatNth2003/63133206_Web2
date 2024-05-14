package com.ntd63133206.bookbuddy.repository;

import com.ntd63133206.bookbuddy.model.Author;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByName(String name);

	Author save(Optional<Author> author);
}
