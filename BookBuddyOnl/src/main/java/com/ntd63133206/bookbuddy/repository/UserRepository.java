package com.ntd63133206.bookbuddy.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import com.ntd63133206.bookbuddy.model.Role;
import com.ntd63133206.bookbuddy.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
    User findByEmail(String email);
    User findByResetPasswordToken(String resetToken);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    @Query("SELECT u FROM User u WHERE (:keyword IS NULL OR LOWER(u.email) LIKE %:keyword% OR LOWER(u.username) LIKE %:keyword%) AND (:roleId IS NULL OR EXISTS (SELECT 1 FROM u.roles r WHERE r.id = :roleId))")
    Page<User> findByKeywordAndRoleId(String keyword, Long roleId, Pageable pageable);

    @Query("SELECT u FROM User u WHERE :keyword IS NULL OR LOWER(u.email) LIKE %:keyword% OR LOWER(u.username) LIKE %:keyword%")
    Page<User> findByKeyword(String keyword, Pageable pageable);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE :roleId IS NULL OR r.id = :roleId")
    Page<User> findByRoleId(Long roleId, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.lastLogin IS NOT NULL ORDER BY u.lastLogin DESC")
    Page<User> findRecentlyLoggedInUsers(Pageable pageable);
    Page<User> findAllByOrderByLastLoginDesc(Pageable pageable);

}
