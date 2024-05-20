package com.ntd63133206.bookbuddy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ntd63133206.bookbuddy.model.Book;
import com.ntd63133206.bookbuddy.model.Purchase;
import com.ntd63133206.bookbuddy.model.User;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    Optional<Purchase> findByUserAndBook(User user, Book book);
}