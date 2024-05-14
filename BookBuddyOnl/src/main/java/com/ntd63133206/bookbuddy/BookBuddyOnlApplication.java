package com.ntd63133206.bookbuddy;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import com.ntd63133206.bookbuddy.service.UserService;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class BookBuddyOnlApplication {
	
	@Autowired
    private UserService userService;
	public static void main(String[] args) {
		SpringApplication.run(BookBuddyOnlApplication.class, args);
	}
	@PostConstruct
    public void init() {
        createDefaultRoles();
    }

	private void createDefaultRoles() {
		userService.createDefaultUserAndRole();
	}

}
