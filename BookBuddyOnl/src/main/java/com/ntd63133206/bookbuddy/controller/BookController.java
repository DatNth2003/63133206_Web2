package com.ntd63133206.bookbuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ntd63133206.bookbuddy.model.Book;
import com.ntd63133206.bookbuddy.service.BookService;

import java.io.IOException;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/add-book")
    public String showAddBookForm(@ModelAttribute Book book) {
        return "books/add-book";
    }
    
    @PostMapping("/add-book")
    public String addBook(@ModelAttribute Book book, @RequestParam("coverImageFile") MultipartFile coverImage) {
        try {
            bookService.addBook(book, coverImage);
            return "redirect:/books/add-book?success";
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/books/add-book?error"; 
        }
    }

    @GetMapping
    public ResponseEntity<Page<Book>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String author) {
        try {
            Pageable paging = PageRequest.of(page, size);
            Page<Book> books;
            if (author != null) {
                books = bookService.findByAuthor(author, paging);
            } else {
                books = bookService.getAllBooks(paging);
            }
            return ResponseEntity.ok().body(books); // Sử dụng ResponseEntity.ok() để trả về HTTP status 200 OK và set body là danh sách sách
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Trả về HTTP status 500 INTERNAL_SERVER_ERROR nếu có lỗi
        }
    }
}
