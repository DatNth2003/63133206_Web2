package com.ntd63133206.bookbuddy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ntd63133206.bookbuddy.model.Book;
import com.ntd63133206.bookbuddy.model.Favorite;
import com.ntd63133206.bookbuddy.model.User;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUser(User user);
    void deleteByUserAndBook(User user, Book book);
    boolean existsByUserAndBook(User user, Book book);
}