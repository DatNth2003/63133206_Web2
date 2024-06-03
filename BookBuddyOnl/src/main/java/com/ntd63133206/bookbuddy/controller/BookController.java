package com.ntd63133206.bookbuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ntd63133206.bookbuddy.dto.BookSearchCriteria;
import com.ntd63133206.bookbuddy.model.Author;
import com.ntd63133206.bookbuddy.model.Book;
import com.ntd63133206.bookbuddy.model.Tag;
import com.ntd63133206.bookbuddy.service.AuthorService;
import com.ntd63133206.bookbuddy.service.BookService;
import com.ntd63133206.bookbuddy.service.TagService;
import com.ntd63133206.bookbuddy.util.Utility;

import jakarta.validation.Valid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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
    public String addBook(@ModelAttribute("book") @Valid Book book, 
                          @RequestParam("coverImageFile") MultipartFile coverImage, 
                          @RequestParam("pdfFile") MultipartFile pdfFile, 
                          BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
        	 for (FieldError error : bindingResult.getFieldErrors()) {
        	        System.out.println(error.getField() + ": " + error.getDefaultMessage());
        	    }
        	    model.addAttribute("errors", bindingResult.getAllErrors());
        	    return "/admin/books/add-book";
        }
        try {
            bookService.addBook(book, coverImage, pdfFile);
            return "redirect:/admin/books/";
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "An error occurred while uploading files.");
            return "error";
        }
    }
    @GetMapping("/edit/{id}")
    public String showEditBookForm(@PathVariable("id") Long id, Model model) {
        Book book = bookService.getBookById(id);
        List<Author> authors = authorService.findAll();
        List<Tag> tags = tagService.getAllTags();
        
        if(authors == null) {
            authors = new ArrayList<>();
        }
        if(tags == null) {
            tags = new ArrayList<>();
        }
        
        model.addAttribute("book", book);
        model.addAttribute("authors", authors);
        model.addAttribute("tags", tags);
        return "admin/books/edit-book";
    }

    @PostMapping("/edit/{id}")
    public String editBook(@PathVariable Long id, 
                           @ModelAttribute Book book,
                           @RequestParam("coverImageFile") MultipartFile coverImage, 
                           @RequestParam("pdfFile") MultipartFile pdfFile, 
                           @RequestParam("selectedAuthors") List<Long> authorIds, 
                           Model model) {
        try {
            Set<Author> authors = authorService.getAuthorsByIds(authorIds);
            
            bookService.updateBookDetails(id, book, coverImage, pdfFile, authors);
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "An error occurred while uploading files.");
            return "error";
        }
        return "redirect:/admin/books/";
    }



    @GetMapping(value = {"", "/"})
    public String searchBooks(@ModelAttribute("searchCriteria") BookSearchCriteria searchCriteria, 
                              Model model) {
        int defaultPageSize = 10;
        searchCriteria.setSize(searchCriteria.getSize() == 0 ? defaultPageSize : searchCriteria.getSize());

        if (Utility.isNullOrEmpty(searchCriteria.getSortField())) {
            searchCriteria.setSortField("updatedAt");
        }

        Sort.Direction direction = Sort.Direction.ASC;
        if (searchCriteria.getSortDirection() != null && searchCriteria.getSortDirection().equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }

        Sort sort = Sort.by(direction, searchCriteria.getSortField());

        PageRequest pageable = PageRequest.of(searchCriteria.getPage(), searchCriteria.getSize(), sort);

        Page<Book> books = bookService.searchBooks(searchCriteria, pageable);
        
        List<Author> authors = authorService.findAll();
        List<Tag> tags = tagService.getAllTags();
        
        model.addAttribute("books", books);
        model.addAttribute("searchCriteria", searchCriteria);
        model.addAttribute("authors", authors);
        model.addAttribute("tags", tags);
        
        return "admin/books/book-list";
    }

    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteBookById(id);
            redirectAttributes.addFlashAttribute("message", "Book deleted successfully");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to delete the book");
        }
        return "redirect:/admin/books";
    }

}
