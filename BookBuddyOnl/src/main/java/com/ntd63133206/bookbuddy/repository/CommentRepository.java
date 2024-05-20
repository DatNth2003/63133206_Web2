package com.ntd63133206.bookbuddy.repository;

import com.ntd63133206.bookbuddy.model.Comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByBookId(Long bookId, Pageable pageable);
}
