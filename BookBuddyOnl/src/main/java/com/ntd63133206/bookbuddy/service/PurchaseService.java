package com.ntd63133206.bookbuddy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ntd63133206.bookbuddy.model.Book;
import com.ntd63133206.bookbuddy.model.Purchase;
import com.ntd63133206.bookbuddy.model.User;
import com.ntd63133206.bookbuddy.repository.PurchaseRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;
    
    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

    public void updatePurchaseStatus(Long purchaseId, String statusStr) {
        Optional<Purchase> optionalPurchase = purchaseRepository.findById(purchaseId);
        if (optionalPurchase.isPresent()) {
            Purchase purchase = optionalPurchase.get();
            Purchase.Status status = Purchase.Status.valueOf(statusStr);
            purchase.setStatus(status);
            purchaseRepository.save(purchase);
        } else {
            throw new IllegalArgumentException("Purchase not found with ID: " + purchaseId);
        }
    }

    public Purchase createPurchase(User user, Book book) {
        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase.setBook(book);
        purchase.setPurchaseDate(LocalDateTime.now());
        purchase.setStatus(Purchase.Status.PENDING);

        return purchaseRepository.save(purchase);
    }

    public Optional<Purchase> findByUserAndBook(User user, Book book) {
        return purchaseRepository.findByUserAndBook(user, book);
    }

    public Purchase updateStatus(Long purchaseId, Purchase.Status status) {
        Optional<Purchase> optionalPurchase = purchaseRepository.findById(purchaseId);
        if (optionalPurchase.isPresent()) {
            Purchase purchase = optionalPurchase.get();
            purchase.setStatus(status);
            return purchaseRepository.save(purchase);
        } else {
            throw new IllegalArgumentException("Purchase not found");
        }
    }
}
