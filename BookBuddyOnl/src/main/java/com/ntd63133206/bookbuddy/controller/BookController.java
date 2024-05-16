package com.ntd63133206.bookbuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ntd63133206.bookbuddy.model.Author;
import com.ntd63133206.bookbuddy.model.Book;
import com.ntd63133206.bookbuddy.model.Tag;
import com.ntd63133206.bookbuddy.service.AuthorService;
import com.ntd63133206.bookbuddy.service.BookService;
import com.ntd63133206.bookbuddy.service.TagService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("/admin/books")
public class BookController {

    @Autowired
    private BookService bookService;
    @Autowired
    private AuthorService authorService;
    @Autowired
    private TagService tagService;
    
    @GetMapping("/add")
    public String showAddBookForm(Model model) {
        List<Author> authors = authorService.findAll();
        List<Tag> tags = tagService.getAllTags();
        model.addAttribute("authors", authors);
        model.addAttribute("tags", tags);
        return "/admin/books/add-book";
    }

    @PostMapping("/add")
    public String addBook(@ModelAttribute Book book,
                          @RequestParam("coverImageFile") MultipartFile coverImage,
                          @RequestParam("pdfFile") MultipartFile pdfFile,
                          @RequestParam("selectedAuthors") List<Long> authorIds,
                          @RequestParam("selectedTags") List<Long> tagIds,
                          RedirectAttributes redirectAttributes) {
        try {
            for (Long authorId : authorIds) {
                Optional<Author> authorOpt = authorService.findById(authorId);
                authorOpt.ifPresent(author -> book.getAuthors().add(author));
            }
            
            for (Long tagId : tagIds) {
                Optional<Tag> tagOpt = tagService.findById(tagId);
                tagOpt.ifPresent(tag -> book.getTags().add(tag));
            }
            
            if (!coverImage.isEmpty()) {
                String coverImagePath = saveCoverImage(coverImage);
                book.setCoverImage(coverImagePath.getBytes());
            }
            if (!pdfFile.isEmpty()) {
                byte[] pdfContent = pdfFile.getBytes();
                book.setPdfContent(pdfContent);
            }
            bookService.save(book);

            redirectAttributes.addFlashAttribute("successMessage", "Book added successfully.");
            return "redirect:/admin/books/add";
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding book. Please try again.");
            return "redirect:/admin/books/add";
        }
    }

    private String saveCoverImage(MultipartFile coverImage) throws IOException {

        String uploadDir = "uploads/book-covers/";
        String fileName = UUID.randomUUID().toString() + "_" + coverImage.getOriginalFilename();
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = coverImage.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        return uploadDir + fileName;
    }





    @GetMapping(value = {"/index", "/"})
    public String getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String sort,
            Model model) {
        try {
            if (page < 0) {
                page = 0;
            }

            Page<Book> books;
            if (author != null && !author.isEmpty()) {
                books = bookService.findByAuthor(author, PageRequest.of(page, size));
            } else {
                Pageable paging;
                if (sort != null && sort.equals("price")) {
                    paging = PageRequest.of(page, size, Sort.by("price"));
                } else {
                    paging = PageRequest.of(page, size, Sort.by("title"));
                }
                books = bookService.getAllBooks(paging);
            }

            model.addAttribute("books", books.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", books.getTotalPages());
            return "/admin/books/book-list";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }



    @GetMapping("/delete-book/{id}")
    public String deleteBook(@PathVariable("id") Long id) {
        bookService.deleteBook(id);
        return "redirect:/admin/books";
    }


    @GetMapping("/edit-book/{id}")
    public String showEditBookForm(@PathVariable("id") Long id, Model model) {
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);
        return "/admin/books/edit-book";
    }

    @PostMapping("/edit-book/{id}")
    public String editBook(@PathVariable("id") Long id, @ModelAttribute Book book, @RequestParam("coverImageFile") MultipartFile coverImage) {
        try {
            bookService.editBook(id, book, coverImage);
            return "redirect:/books/book-list";
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/admin/books/edit-book/" + id + "?error";
        }
    }

    


}
