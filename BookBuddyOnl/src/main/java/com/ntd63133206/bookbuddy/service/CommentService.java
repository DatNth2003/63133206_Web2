package com.ntd63133206.bookbuddy.service;

import com.ntd63133206.bookbuddy.model.Book;
import com.ntd63133206.bookbuddy.model.Comment;
import com.ntd63133206.bookbuddy.model.User;
import com.ntd63133206.bookbuddy.repository.BookRepository;
import com.ntd63133206.bookbuddy.repository.CommentRepository;
import com.ntd63133206.bookbuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;

    public Comment add(Long bookId, Long userId, String content) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new IllegalArgumentException("Book not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        Comment comment = new Comment();
        comment.setBook(book);
        comment.setUser(user);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    public void delete(Long commentId) {
        commentRepository.deleteById(commentId);
    }
    public Page<Comment> getCommentsByBookId(Long bookId, int page, int size, String sortBy, String orderBy) {
        Sort.Direction direction = Sort.Direction.fromString(orderBy);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Comment> commentsPage = commentRepository.findByBookId(bookId, pageable);

        List<Comment> comments = commentsPage.getContent().stream()
                .filter(comment -> comment.getUser() != null && userRepository.existsById(comment.getUser().getId()))
                .collect(Collectors.toList());

        return new PageImpl<>(comments, pageable, comments.size());
    }





}
