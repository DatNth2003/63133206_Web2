package com.ntd63133206.bookbuddy.service;

import com.ntd63133206.bookbuddy.model.User;

public interface UserService {
	User save (User userDto);
	User findByEmail(String email);
	User findByUsername(String username);
}
