package com.ntd63133206.bookbuddy.service;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.ntd63133206.bookbuddy.repository.UserRepository;
import com.ntd63133206.bookbuddy.model.User;


public class CustomUserDetailsService implements UserDetailsService {
	
	@Autowired
	 private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(username);
		if (user == null) {
           System.out.println("CustomUserDetailsService: không tìm thấy user " + username);
			throw new UsernameNotFoundException("Không tìm thấy user");
			
		}
		System.out.println("CustomUserDetailsService: đã tìm thấy user " + username);
		return new CustomUserDetail(user);

	}

}
