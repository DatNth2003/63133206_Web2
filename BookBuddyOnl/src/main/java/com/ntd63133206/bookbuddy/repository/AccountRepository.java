package com.ntd63133206.bookbuddy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ntd63133206.bookbuddy.model.User;

@Repository
public interface AccountRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
    User findByEmail(String email);
    User findByResetToken(String resetToken);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);


}
