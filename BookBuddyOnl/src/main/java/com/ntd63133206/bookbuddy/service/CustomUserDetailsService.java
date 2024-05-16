package com.ntd63133206.bookbuddy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ntd63133206.bookbuddy.repository.UserRepository;
import com.ntd63133206.bookbuddy.model.CustomUserDetails;
import com.ntd63133206.bookbuddy.model.User;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new CustomUserDetails(user);
    }


//    public User getByResetPasswordToken(String token) {
//        return userRepository.findByResetPasswordToken(token);
//    }
//
//    public void updatePassword(User user, String newPassword) {
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        String encodedPassword = passwordEncoder.encode(newPassword);
//        user.setPassword(encodedPassword);
//
//        user.setResetPasswordToken(null);
//        userRepository.save(user);
//    }
}
