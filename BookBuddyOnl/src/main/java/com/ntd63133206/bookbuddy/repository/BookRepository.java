package com.ntd63133206.bookbuddy.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ntd63133206.bookbuddy.dto.BookSearchCriteria;
import com.ntd63133206.bookbuddy.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Page<Book> findByTitleContaining(String title, Pageable pageable);

    Page<Book> findByPrice(double price, Pageable pageable);

    Page<Book> findByAuthorsName(String authorName, Pageable pageable);

    
    List<Book> findTop10ByOrderByUpdatedAtDesc(Pageable pageable);

    List<Book> findTop10ByOrderByUpdatedAtDesc();

    @Query("SELECT b FROM Book b " +
            "LEFT JOIN b.authors a " + 
            "WHERE (:keyword IS NULL OR b.title LIKE %:keyword% OR b.description LIKE %:keyword%) " +
            "AND (:authorIds IS NULL OR a.id IN :authorIds) " +
            "AND (:tagIds IS NULL OR EXISTS (SELECT t FROM Tag t WHERE t.id IN :tagIds AND t MEMBER OF b.tags)) " +
            "AND (:minPrice IS NULL OR b.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR b.price <= :maxPrice)")
     Page<Book> searchBooks(@Param("keyword") String keyword,
                            @Param("authorIds") Set<Long> authorIds,
                            @Param("tagIds") Set<Long> tagIds,
                            @Param("minPrice") Double minPrice,
                            @Param("maxPrice") Double maxPrice,
                            Pageable pageable);
}