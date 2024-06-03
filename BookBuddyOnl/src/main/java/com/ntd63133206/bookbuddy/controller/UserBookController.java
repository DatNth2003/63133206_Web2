package com.ntd63133206.bookbuddy.controller;

import java.security.Principal;
import java.time.LocalDateTime;
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
    @GetMapping("/my-books")
    public String showUserBooks(Model model, @AuthenticationPrincipal User user) {
        if (user != null) {
            List<Book> favoriteBooks = favoriteService.getFavoriteBooks(user.getUsername());
            List<Book> purchasedBooks = purchaseService.getPurchasedBooks(user.getUsername());
            model.addAttribute("favoriteBooks", favoriteBooks);
            model.addAttribute("purchasedBooks", purchasedBooks);
        }
        return "books/my-books";
    }
    @GetMapping(value = {"", "/"})
    public String searchBooks(@ModelAttribute("searchCriteria") BookSearchCriteria searchCriteria, 
                              @AuthenticationPrincipal User user, 
                              Model model) {
        int pageSize = 10;
        PageRequest pageable = PageRequest.of(searchCriteria.getPage(), pageSize);
        Page<Book> books = bookService.searchBooks(searchCriteria, pageable);
        
        List<Author> authors = authorService.findAll();
        List<Tag> tags = tagService.getAllTags();
        
        if (user != null) {
            String username = user.getUsername();
            books.forEach(book -> {
            	boolean isFavorite = favoriteService.isBookFavoriteForUser(book.getId(), username);
                book.setFavorite(isFavorite);
            });
        }
        model.addAttribute("books", books);
        model.addAttribute("searchCriteria", searchCriteria);
        model.addAttribute("authors", authors);
        model.addAttribute("tags", tags);
        
        return "books/search-book";
    }

    @GetMapping("/read/{bookId}")
    public String readBook(@PathVariable Long bookId, Model model, @AuthenticationPrincipal User user) {
        Book book = bookService.findById(bookId)
                               .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + bookId));

        if (book.getPrice() == 0 || purchaseService.isBookPurchasedByUser(bookId, user.getUsername())) {
            String pdfPath = "/pdfs/" + book.getPdfPath();
            model.addAttribute("book", book);
            model.addAttribute("pdfPath", pdfPath);
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
                                   @AuthenticationPrincipal User user,
                                   @RequestParam(value = "purchaseSuccessMessage", required = false) String purchaseSuccessMessage,
                                   @RequestParam(name = "page", defaultValue = "0") int page,
                                   @RequestParam(name = "size", defaultValue = "10") int size,
                                   @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                   @RequestParam(name = "orderBy", defaultValue = "desc") String orderBy) {

        Book book = bookService.getBookById(bookId);
        if (book == null) {
            model.addAttribute("errorMessage", "Sách không tồn tại.");
            return "books/book-details";
        }

        Set<Author> authors = book.getAuthors();
        Set<Tag> tags = book.getTags();
        Page<Comment> commentPage = commentService.getCommentsByBookId(bookId, page, size, sortBy, orderBy);
        model.addAttribute("book", book);
        model.addAttribute("authors", authors);
        model.addAttribute("tags", tags);
        model.addAttribute("size", size);
        model.addAttribute("comments", commentPage.getContent());
        model.addAttribute("totalPages", commentPage.getTotalPages());
        model.addAttribute("currentPage", commentPage.getNumber());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("orderBy", orderBy);

        boolean isBookInFavorites = false;
        if (user != null) {
            System.out.print(user.getUsername() + " đang xem chi tiết sách!");
            isBookInFavorites = favoriteService.isBookFavoriteForUser(bookId, user.getUsername());
        } else {
            System.out.print("User bị null!");
        }
        model.addAttribute("isBookInFavorites", isBookInFavorites);

        boolean isBookPaidFor = false;
        if (user != null && user.getId() != null) {
            if (book.getPrice() > 0) {
                isBookPaidFor = purchaseService.isBookPurchasedByUser(bookId, user.getUsername());
                System.out.println("Người dùng đã mua: " + isBookPaidFor);
            } else {
                isBookPaidFor = true;
            }
        } else {
            System.out.println("Người dùng chưa đăng nhập nên chưa mua!");
        }

        model.addAttribute("isBookPaidFor", isBookPaidFor);

        if (user != null && user.getBalance() < book.getPrice() && book.getPrice() > 0) {
            model.addAttribute("insufficientBalanceMessage", "Số dư không đủ để mua cuốn sách này");
        }
        if (purchaseSuccessMessage != null) {
            model.addAttribute("purchaseSuccessMessage", purchaseSuccessMessage);
        }
        return "books/book-details";
    }






}
