package com.ntd63133206.bookbuddy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ntd63133206.bookbuddy.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}