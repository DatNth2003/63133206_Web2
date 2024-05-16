package com.ntd63133206.bookbuddy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ntd63133206.bookbuddy.dto.BookSearchCriteria;
import com.ntd63133206.bookbuddy.model.Author;
import com.ntd63133206.bookbuddy.model.Book;
import com.ntd63133206.bookbuddy.model.Tag;
import com.ntd63133206.bookbuddy.service.AuthorService;
import com.ntd63133206.bookbuddy.service.BookService;
import com.ntd63133206.bookbuddy.service.TagService;

@Controller
@RequestMapping("/books")
public class UserBookController {
	@Autowired
	private BookService bookService;
	  
	@Autowired
	private AuthorService authorService;
			    
	@Autowired
	private TagService tagService;
	@GetMapping("/")
    public String searchBooks(@ModelAttribute("searchCriteria") BookSearchCriteria searchCriteria, Model model) {
        searchCriteria.setPage(0);

        int pageSize = 10;
        
        PageRequest pageable = PageRequest.of(searchCriteria.getPage(), pageSize);
        Page<Book> books = bookService.searchBooks(searchCriteria, pageable);
        
        List<Author> authors = authorService.findAll();
        List<Tag> tags = tagService.getAllTags();
        
        model.addAttribute("books", books);
        model.addAttribute("searchCriteria", searchCriteria);
        model.addAttribute("authors", authors);
        model.addAttribute("tags", tags);
        
        return "books/search-book";
    }

    @GetMapping("/read")
    public String readBook(Model model) {
        return "/books/read-book";
    }

}
