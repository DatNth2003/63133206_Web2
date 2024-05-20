package com.ntd63133206.bookbuddy.repository;

import java.time.LocalDate;
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
    @Query("SELECT b FROM Book b WHERE (:keyword IS NULL OR LOWER(b.title) LIKE %:keyword%) " +
            "AND (:authorIds IS NULL OR EXISTS (SELECT 1 FROM b.authors a WHERE a.id IN :authorIds)) " +
            "AND (:tagIds IS NULL OR EXISTS (SELECT 1 FROM b.tags t WHERE t.id IN :tagIds)) " +
            "AND (:minPrice IS NULL OR b.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR b.price <= :maxPrice) " +
            "AND (:updatedAtStart IS NULL OR b.updatedAt >= :updatedAtStart) " +
            "AND (:updatedAtEnd IS NULL OR b.updatedAt <= :updatedAtEnd)")
     Page<Book> searchBooks(@Param("keyword") String keyword,
                            @Param("authorIds") Set<Long> authorIds,
                            @Param("tagIds") Set<Long> tagIds,
                            @Param("minPrice") Double minPrice,
                            @Param("maxPrice") Double maxPrice,
                            @Param("updatedAtStart") LocalDate updatedAtStart,
                            @Param("updatedAtEnd") LocalDate updatedAtEnd,
                            Pageable pageable);


    List<Book> findTop10ByOrderByUpdatedAtDesc(Pageable pageable);

    List<Book> findTop10ByOrderByUpdatedAtDesc();

}