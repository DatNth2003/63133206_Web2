package com.ntd63133206.bookbuddy.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ntd63133206.bookbuddy.dto.BookSearchCriteria;
import com.ntd63133206.bookbuddy.model.Author;
import com.ntd63133206.bookbuddy.model.Book;
import com.ntd63133206.bookbuddy.model.Comment;
import com.ntd63133206.bookbuddy.model.CustomUserDetails;
import com.ntd63133206.bookbuddy.model.Purchase;
import com.ntd63133206.bookbuddy.model.Tag;
import com.ntd63133206.bookbuddy.model.User;
import com.ntd63133206.bookbuddy.service.AuthorService;
import com.ntd63133206.bookbuddy.service.BookService;
import com.ntd63133206.bookbuddy.service.CommentService;
import com.ntd63133206.bookbuddy.service.FavoriteService;
import com.ntd63133206.bookbuddy.service.PurchaseService;
import com.ntd63133206.bookbuddy.service.TagService;
import com.ntd63133206.bookbuddy.service.UserService;

@Controller
@RequestMapping("/books")
public class UserBookController {
	@Autowired
	private BookService bookService;
	  
	@Autowired
	private AuthorService authorService;
			    
	@Autowired
	private TagService tagService;
	
    @Autowired
    private FavoriteService favoriteService;
    
    @Autowired
	private CommentService commentService;
    
    @Autowired
	private UserService userService;
    
    @Autowired
	private PurchaseService purchaseService;
    
    @GetMapping(value = {"", "/"})
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
    public String readPdf(Model model) {
        String pdfPath = "4093492f-b2cb-4798-b357-35322d455b56_Test.pdf";
        model.addAttribute("pdfPath", pdfPath);
        return "/books/read-book";
    }

	@GetMapping("/read/{bookId}")
    public String readBook(@PathVariable Long bookId, Model model, Principal principal) {
        Optional<Book> optionalBook = bookService.findById(bookId);

        if (!optionalBook.isPresent()) {
            System.out.println("Không tìm thấy sách có ID: " + bookId);
            return "redirect:/books";
        }

        Book book = optionalBook.get();
        String username = principal.getName();
        User user = userService.getUserByUsername(username);
        
        Optional<Purchase> optionalPurchase = purchaseService.findByUserAndBook(user, book);

        if (optionalPurchase.isPresent() && optionalPurchase.get().getStatus() == Purchase.Status.SUCCESS) {
            String pdfPath = "pdfs/" + book.getPdfPath();
            model.addAttribute("pdfPath", pdfPath);
            model.addAttribute("book", book);
            System.out.println("Load pdf: " + pdfPath);

            return "books/read-book";
        } else {
            System.out.println("Người dùng chưa mua sách hoặc giao dịch chưa thành công cho sách có ID: " + bookId);
            return "redirect:/books";
        }
    }


	@GetMapping("/details/{bookId}")
	public String showBookDetails(@PathVariable("bookId") Long bookId,
	                               Model model,
	                               @AuthenticationPrincipal CustomUserDetails customUserDetails,
	                               @RequestParam(name = "page", defaultValue = "0") int page,
	                               @RequestParam(name = "size", defaultValue = "10") int size,
	                               @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
	                               @RequestParam(name = "orderBy", defaultValue = "desc") String orderBy) {
	    if (customUserDetails == null) {
	        return "redirect:/account/login";
	    }

	    Book book = bookService.getBookById(bookId);
	    if (book == null) {
	        model.addAttribute("errorMessage", "Sách không tồn tại.");
	        return "books/book-details";
	    }
	    Set<Author> authors = book.getAuthors();
	    Set<Tag> tags = book.getTags();
	    Page<Comment> commentPage = commentService.getCommentsByBookId(bookId, page, size, sortBy, orderBy);
	    boolean isFavorite = favoriteService.isBookFavoriteForUser(bookId, customUserDetails.getUsername());
	    model.addAttribute("book", book);
	    model.addAttribute("isFavorite", isFavorite);
	    model.addAttribute("authors", authors);
	    model.addAttribute("tags", tags);
	    model.addAttribute("comments", commentPage.getContent()); // Thêm danh sách comment vào model
	    model.addAttribute("totalPages", commentPage.getTotalPages());
	    model.addAttribute("currentPage", commentPage.getNumber());
	    model.addAttribute("sortBy", sortBy);
	    model.addAttribute("orderBy", orderBy);

	    return "books/book-details";
	}




}
