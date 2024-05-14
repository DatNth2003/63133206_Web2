package com.ntd63133206.bookbuddy.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ntd63133206.bookbuddy.model.Author;
import com.ntd63133206.bookbuddy.repository.AuthorRepository;

@Service
public class AuthorService {
	@Autowired
    private AuthorRepository authorRepository;

    public Author save(Author author) {
        return authorRepository.save(author);
    }

    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    public Optional<Author> findById(Long id) {
        return authorRepository.findById(id);
    }

    public Optional<Author> findByName(String name) {
        return authorRepository.findByName(name);
    }

    public void deleteById(Long id) {
        authorRepository.deleteById(id);
    }
}
