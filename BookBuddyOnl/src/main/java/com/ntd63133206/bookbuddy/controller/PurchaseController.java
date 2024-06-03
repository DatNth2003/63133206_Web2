package com.ntd63133206.bookbuddy.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ntd63133206.bookbuddy.model.Book;
import com.ntd63133206.bookbuddy.model.Purchase;
import com.ntd63133206.bookbuddy.model.User;
import com.ntd63133206.bookbuddy.service.BookService;
import com.ntd63133206.bookbuddy.service.PurchaseService;
import com.ntd63133206.bookbuddy.service.UserService;

@Controller
@RequestMapping("/purchases")
public class PurchaseController {
	
    @Autowired
    private PurchaseService purchaseService;
    
    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @GetMapping({"", "/", "/index"})
    public String viewPurchases(Model model) {
        List<Purchase> purchases = purchaseService.getAllPurchases();
        model.addAttribute("purchases", purchases);
        return "admin/purchases/purchase-list";
    }

    @PostMapping("/add/{bookId}")
    public String addPurchase(@PathVariable Long bookId,
                              @AuthenticationPrincipal User user,
                              Model model) {
        System.out.println("Xử lý thanh toán sách: " + bookId);
        
        if (user == null) {
            System.out.println("User is null");
            model.addAttribute("errorMessage", "Bạn cần đăng nhập để mua sách.");
            return "redirect:/login";
        }

        String username = user.getUsername();
        System.out.println("Username: " + username);
        Book book = bookService.getBookById(bookId);

        if (book == null) {
            System.out.println("Book is null");
            model.addAttribute("errorMessage", "Sách không tồn tại.");
            return "redirect:/books/details/" + bookId;
        }

        double bookPrice = book.getPrice();
        double userBalance = user.getBalance();

        System.out.println("Book price: " + bookPrice);
        System.out.println("User balance: " + userBalance);

        if (!purchaseService.isBookPurchasedByUser(bookId, user.getUsername())) {
            if (bookPrice > 0 && userBalance >= bookPrice) {
                user.withdraw(bookPrice);
                userService.save(user);

                Purchase purchase = new Purchase();
                purchase.setUser(user);
                purchase.setBook(book);
                purchase.setPurchaseDate(LocalDateTime.now());
                purchaseService.save(purchase);

                return "redirect:/books/details/" + bookId + "?purchaseSuccessMessage=Complete!";
            } else if (bookPrice > 0 && userBalance < bookPrice) {
                System.out.println("User does not have sufficient balance to purchase the book");
                model.addAttribute("errorMessage", "Số dư không đủ để mua cuốn sách này.");
            } else {
                System.out.println("User is purchasing a free book");
                Purchase purchase = purchaseService.createPurchase(user, book);
                if (purchase != null) {
                    return "redirect:/books/details/" + bookId + "?purchaseSuccessMessage=GetFree!";
                } else {
                    model.addAttribute("errorMessage", "Bạn đã mua cuốn sách này rồi.");
                }
            }
        } else {
            model.addAttribute("errorMessage", "Bạn đã mua cuốn sách này rồi.");
        }
        return "redirect:/books/details/" + bookId + "?purchaseSuccessMessage=Failure!";
    }



}
