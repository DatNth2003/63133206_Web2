package com.ntd63133206.bookbuddy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ntd63133206.bookbuddy.model.Book;
import com.ntd63133206.bookbuddy.model.Purchase;
import com.ntd63133206.bookbuddy.model.User;
import com.ntd63133206.bookbuddy.repository.BookRepository;
import com.ntd63133206.bookbuddy.repository.PurchaseRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private BookRepository bookRepository;
    
    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }


    public Purchase createPurchase(User user, Book book) {
        if (isBookPurchasedByUser(book.getId(), user.getUsername())) {
            return null;
        } else {
            Purchase purchase = new Purchase();
            purchase.setUser(user);
            purchase.setBook(book);
            purchase.setPurchaseDate(LocalDateTime.now());
            return purchaseRepository.save(purchase);
        }
    }


    public Optional<Purchase> findByUserAndBook(User user, Book book) {
        return purchaseRepository.findByUserAndBook(user, book);
    }

    public Purchase save(Purchase purchase) {
        if (purchase.getId() == null || !purchaseRepository.existsById(purchase.getId())) {
            return purchaseRepository.save(purchase);
        } else {
            throw new IllegalArgumentException("Id đã tồn tại, không thể lưu lại đối tượng Purchase.");
        }
    }


    public List<Book> getPurchasedBooks(String username) {
        return purchaseRepository.findBooksByUsername(username);
    }
    public boolean isBookPurchasedByUser(Long bookId, String username) {
        return purchaseRepository.existsByBookIdAndUserUsername(bookId, username);
    }
}
