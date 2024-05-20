package com.ntd63133206.bookbuddy.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.ntd63133206.bookbuddy.model.Author;
import com.ntd63133206.bookbuddy.model.Book;
import com.ntd63133206.bookbuddy.model.Tag;
import com.ntd63133206.bookbuddy.model.User;
import com.ntd63133206.bookbuddy.dto.BookSearchCriteria;
import com.ntd63133206.bookbuddy.repository.BookRepository;
import com.ntd63133206.bookbuddy.repository.UserRepository;

@Service
@Transactional
public class BookService {
	private static final String UPLOAD_DIR = "src/main/resources/static/images/books/covers/";
    private static final String PDF_UPLOAD_DIR = "src/main/resources/static/pdf/";
	@Autowired
    private UserRepository userRepository;
    @Autowired
    private BookRepository bookRepository;

    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public Page<Book> findByAuthor(String author, Pageable pageable) {
        return bookRepository.findByAuthorsName(author, pageable);
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }
    public List<Book> get10LatestBooks() {
        return bookRepository.findTop10ByOrderByUpdatedAtDesc();
    }

    public Book addBook(Book book, MultipartFile coverImage, MultipartFile pdfFile) throws IOException {
        saveCoverImage(book, coverImage);
        savePdfFile(book, pdfFile);
        return bookRepository.save(book);
    }

    public Book editBook(Long id, Book updatedBook, MultipartFile coverImage, MultipartFile pdfFile) throws IOException {
        Book existingBook = bookRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + id));
        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setPrice(updatedBook.getPrice());
        existingBook.setDescription(updatedBook.getDescription());

        if (coverImage != null && !coverImage.isEmpty()) {
            saveCoverImage(existingBook, coverImage);
        }
        if (pdfFile != null && !pdfFile.isEmpty()) {
            savePdfFile(existingBook, pdfFile);
        }
        return bookRepository.save(existingBook);
    }
    
    public String saveCoverImage(Book book, MultipartFile coverImage) throws IOException {
        if (coverImage != null && !coverImage.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "." + getFileExtension(coverImage.getOriginalFilename());
            String coverImagePath = UPLOAD_DIR + fileName;
            byte[] coverImageBytes = coverImage.getBytes();
            Path path = Paths.get(coverImagePath);
            Files.write(path, coverImageBytes);
            book.setCoverImage(fileName);
            return fileName;
        }
        return null;
    }

    private String getFileExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf('.');
        if (lastIndex != -1) {
            return fileName.substring(lastIndex + 1);
        }
        return "";
    }


    public String savePdfFile(Book book, MultipartFile pdfFile) throws IOException {
        if (pdfFile != null && !pdfFile.isEmpty()) {
            String fileName = UUID.randomUUID().toString();
            String pdfFilePath = PDF_UPLOAD_DIR + fileName;
            byte[] pdfFileBytes = pdfFile.getBytes();
            Path path = Paths.get(pdfFilePath);
            Files.write(path, pdfFileBytes);
            book.setPdfPath(fileName);
            return fileName;
        }
        return null;
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

    public void save(Book book) {
        bookRepository.save(book);
    }

    public Page<Book> searchBooks(BookSearchCriteria searchCriteria, Pageable pageable) {
        return bookRepository.searchBooks(searchCriteria.getKeyword(),
                                           searchCriteria.getAuthorIds(),
                                           searchCriteria.getTagIds(),
                                           searchCriteria.getMinPrice(),
                                           searchCriteria.getMaxPrice(),
                                           searchCriteria.getUpdatedAtStart(),
                                           searchCriteria.getUpdatedAtEnd(),
                                           pageable);
    }

    public Optional<Book> findById(Long bookId) {
         return bookRepository.findById(bookId);
    }
    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
    }
    
    private void updateCoverImage(Long id, Book book, MultipartFile coverImage) throws IOException {
        String oldCoverImagePath = book.getCoverImage();
        if (oldCoverImagePath != null && !oldCoverImagePath.isEmpty()) {
            Path oldCoverImageFile = Paths.get(UPLOAD_DIR + oldCoverImagePath);
            Files.deleteIfExists(oldCoverImageFile);
        }

        // Save the new cover image and update the book entity
        String newCoverImagePath = saveCoverImage(book, coverImage); // Truyền vào book
        book.setCoverImage(newCoverImagePath);
    }

    private void updatePdf(Long id, Book book, MultipartFile pdfFile) throws IOException {
        String oldPdfPath = book.getPdfPath();
        if (oldPdfPath != null && !oldPdfPath.isEmpty()) {
            Path oldPdfFile = Paths.get(PDF_UPLOAD_DIR + oldPdfPath);
            Files.deleteIfExists(oldPdfFile);
        }

        String newPdfPath = savePdfFile(book, pdfFile); // Truyền vào book
        book.setPdfPath(newPdfPath);
    }

    public void updateBookDetails(Long id, Book updatedBook, MultipartFile coverImage, MultipartFile pdfFile) throws IOException {
        Book existingBook = bookRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + id));

        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setPrice(updatedBook.getPrice());
        existingBook.setDescription(updatedBook.getDescription());

        if (coverImage != null && !coverImage.isEmpty()) {
            updateCoverImage(id, existingBook, coverImage); // Sử dụng updateCoverImage đã sửa
        }

        if (pdfFile != null && !pdfFile.isEmpty()) {
            updatePdf(id, existingBook, pdfFile); // Sử dụng updatePdf đã sửa
        }

        bookRepository.save(existingBook);
    }

}