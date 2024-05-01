package com.ntd63133206.bookbuddy.service;

import java.io.IOException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ntd63133206.bookbuddy.model.Author;
import com.ntd63133206.bookbuddy.model.Book;
import com.ntd63133206.bookbuddy.model.Tag;
import com.ntd63133206.bookbuddy.repository.BookRepository;

@Service
@Transactional
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public Page<Book> findByAuthor(String author, Pageable pageable) {
        return bookRepository.findByAuthorsNameContaining(author, pageable);
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public Book addBook(Book book, MultipartFile coverImage) throws IOException {
        if (coverImage != null && !coverImage.isEmpty()) {
            byte[] imageData = coverImage.getBytes();
            book.setCoverImage(imageData);
        }
        return bookRepository.save(book);
    }

    public Book editBook(Long id, Book updatedBook, MultipartFile coverImage) throws IOException {
        Book existingBook = getBookById(id);
        if (existingBook != null) {
            existingBook.setTitle(updatedBook.getTitle());
            existingBook.setPrice(updatedBook.getPrice());
            existingBook.setDescription(updatedBook.getDescription());

            Set<Author> updatedAuthors = updatedBook.getAuthors();
            if (updatedAuthors != null && !updatedAuthors.isEmpty()) {
                existingBook.setAuthors(updatedAuthors);
            }
            
            Set<Tag> updatedTags = updatedBook.getTags();
            if (updatedTags != null) {
                existingBook.setTags(updatedTags);
            }

            if (coverImage != null && !coverImage.isEmpty()) {
                existingBook.setCoverImage(coverImage.getBytes());
            }

            return bookRepository.save(existingBook);
        } else {
            throw new IllegalArgumentException("Book not found with ID: " + id);
        }
    }



    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public void addTagToBook(Long bookId, Tag tag) {
        bookRepository.findById(bookId).ifPresent(book -> {
            book.getTags().add(tag);
            bookRepository.save(book);
        });
    }

    public void removeTagFromBook(Long bookId, Tag tag) {
        bookRepository.findById(bookId).ifPresent(book -> {
            book.getTags().remove(tag);
            bookRepository.save(book);
        });
    }
}
