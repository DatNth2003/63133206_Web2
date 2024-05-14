package com.ntd63133206.bookbuddy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ntd63133206.bookbuddy.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Page<Book> findByTitleContaining(String title, Pageable pageable);

    Page<Book> findByPrice(double price, Pageable pageable);

    Page<Book> findByAuthorsName(String authorName, Pageable pageable);
}