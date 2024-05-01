package com.ntd63133206.bookbuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    @GetMapping("/delete-book/{id}")
    public String deleteBook(@PathVariable("id") Long id) {
        bookService.deleteBook(id);
        return "redirect:/books/book-list";
    }

    @GetMapping("/edit-book/{id}")
    public String showEditBookForm(@PathVariable("id") Long id, Model model) {
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);
        return "books/edit-book";
    }

    @PostMapping("/edit-book/{id}")
    public String editBook(@PathVariable("id") Long id, @ModelAttribute Book book, @RequestParam("coverImageFile") MultipartFile coverImage) {
        try {
            bookService.editBook(id, book, coverImage);
            return "redirect:/books/book-list";
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/books/edit-book/" + id + "?error";
        }
    }

    @GetMapping("/list")
    public String getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String author,
            Model model) {
        try {
            Pageable paging = PageRequest.of(page, size);
            Page<Book> books;
            if (author != null) {
                books = bookService.findByAuthor(author, paging);
            } else {
                books = bookService.getAllBooks(paging);
            }
            model.addAttribute("books", books.getContent());
            return "/books/book-list";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

}
