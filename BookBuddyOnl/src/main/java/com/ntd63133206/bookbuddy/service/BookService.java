package com.ntd63133206.bookbuddy.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ntd63133206.bookbuddy.model.Book;
import com.ntd63133206.bookbuddy.repository.BookRepository;

@Service
public class BookService {
	@Autowired
    private BookRepository bookRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        return optionalBook.orElse(null);
    }

    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    public Book updateBook(Long id, Book updatedBook) {
        Book existingBook = getBookById(id);
        if (existingBook != null) {
            existingBook.setTitle(updatedBook.getTitle());
            existingBook.setAuthor(updatedBook.getAuthor());
            // Cập nhật các trường khác của sách nếu cần
            return bookRepository.save(existingBook);
        }
        return null;
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public void addTagToBook(Long bookId, Tag tag) {
        Book book = getBookById(bookId);
        if (book != null) {
            book.getTags().add(tag);
            bookRepository.save(book);
        }
    }

    public void removeTagFromBook(Long bookId, Tag tag) {
        Book book = getBookById(bookId);
        if (book != null) {
            book.getTags().remove(tag);
            bookRepository.save(book);
        }
    }
}