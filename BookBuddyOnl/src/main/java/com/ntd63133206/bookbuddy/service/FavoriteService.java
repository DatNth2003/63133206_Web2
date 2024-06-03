package com.ntd63133206.bookbuddy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ntd63133206.bookbuddy.model.Book;
import com.ntd63133206.bookbuddy.model.Favorite;
import com.ntd63133206.bookbuddy.model.User;
import com.ntd63133206.bookbuddy.repository.BookRepository;
import com.ntd63133206.bookbuddy.repository.FavoriteRepository;
import com.ntd63133206.bookbuddy.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

	@Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    public List<Book> getFavoriteBooks(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            List<Favorite> favorites = favoriteRepository.findByUser(user);
            return favorites.stream()
                            .map(Favorite::getBook)
                            .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Transactional
    public void toggleFavorite(Long bookId, String username) {
        User user = userRepository.findByUsername(username);
        Book book = bookRepository.findById(bookId).orElse(null);
        if (user != null && book != null) {
            boolean isFavorite = favoriteRepository.existsByUserAndBook(user, book);
            if (isFavorite) {
                System.out.println("Hủy Fav cho " + username + " với sách ID " + bookId);
                favoriteRepository.deleteByUserAndBook(user, book);
            } else {
                System.out.println("Tạo Fav cho " + username + " với sách ID " + bookId);
                Favorite favorite = new Favorite();
                favorite.setUser(user);
                favorite.setBook(book);
                favorite.setFavDate(LocalDateTime.now());
                favoriteRepository.save(favorite);
            }
        }
    }


    public boolean isBookFavoriteForUser(Long bookId, String username) {
        User user = userRepository.findByUsername(username);
        Book book = bookRepository.findById(bookId).orElse(null);
        if (user != null && book != null) {
            return favoriteRepository.existsByUserAndBook(user, book);
        }
        return false;
    }

    public Book getBookById(Long bookId) {
        return bookRepository.findById(bookId).orElse(null);
    }
}