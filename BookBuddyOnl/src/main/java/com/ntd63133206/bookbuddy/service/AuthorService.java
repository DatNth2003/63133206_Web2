package com.ntd63133206.bookbuddy.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.ntd63133206.bookbuddy.model.Author;
import com.ntd63133206.bookbuddy.repository.AuthorRepository;

@Service
public class AuthorService {

    private static final String UPLOAD_DIR = "src/main/resources/static/images/authors/";

    @Autowired
    private AuthorRepository authorRepository;

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

    public Page<Author> searchAuthorsByKeyword(String keyword, Pageable pageable) {
        return authorRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }

    public String saveAuthorImage(MultipartFile authorImage) {
        try {
            if (authorImage.isEmpty()) {
                throw new IllegalArgumentException("Author image file is empty");
            }

            System.out.println("Đang tải ảnh: " + authorImage.getOriginalFilename());

            String originalFileName = authorImage.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String filename = "authorimage" + UUID.randomUUID().toString() + fileExtension;
            Path directory = Paths.get(UPLOAD_DIR);

            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
                System.out.println("Created directory: " + directory.toString());
            }

            Path path = directory.resolve(filename);
            Files.copy(authorImage.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Saved Author Image: " + path.toString());

            return filename;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    public Author save(Author author) {
        System.out.println("Saving author: " + author);
        return authorRepository.save(author);
    }
    


}
