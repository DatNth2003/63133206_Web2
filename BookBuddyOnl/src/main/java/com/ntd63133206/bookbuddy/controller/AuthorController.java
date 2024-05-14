package com.ntd63133206.bookbuddy.controller;

import com.ntd63133206.bookbuddy.model.Author;
import com.ntd63133206.bookbuddy.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/admin/authors")
public class AuthorController {

    private static final String UPLOAD_DIR = "src/main/resources/static/images/authors/";

    @Autowired
    private AuthorService authorService;

    @GetMapping
    public String listAuthors(Model model) {
        model.addAttribute("authors", authorService.findAll());
        return "admin/authors/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("author", new Author());
        return "admin/authors/add";
    }

    @PostMapping("/add")
    public String addAuthor(@ModelAttribute Author author,
                            @RequestParam("authorImage") MultipartFile authorImage) throws IOException {
        if (!authorImage.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + authorImage.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR, fileName);
            Files.write(filePath, authorImage.getBytes());
            author.setAuthorImage(fileName);
        }
        authorService.save(author);
        return "redirect:/admin/authors";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Author> author = authorService.findById(id);
        if (author.isPresent()) {
            model.addAttribute("author", author.get());
            return "admin/authors/edit";
        }
        return "redirect:/admin/authors";
    }

    @PostMapping("/edit/{id}")
    public String editAuthor(@PathVariable Long id, @ModelAttribute Author author,
                             @RequestParam("authorImage") MultipartFile authorImage) throws IOException {
        if (!authorImage.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + authorImage.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR, fileName);
            Files.write(filePath, authorImage.getBytes());
            author.setAuthorImage(fileName);
        } else {
            Optional<Author> existingAuthor = authorService.findById(id);
            existingAuthor.ifPresent(value -> author.setAuthorImage(value.getAuthorImage()));
        }
        author.setId(id);
        authorService.save(author);
        return "redirect:/admin/authors";
    }

    @GetMapping("/delete/{id}")
    public String deleteAuthor(@PathVariable Long id) {
        authorService.deleteById(id);
        return "redirect:/admin/authors";
    }
}
