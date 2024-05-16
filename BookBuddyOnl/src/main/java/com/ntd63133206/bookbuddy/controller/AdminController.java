package com.ntd63133206.bookbuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import com.ntd63133206.bookbuddy.model.User;
import com.ntd63133206.bookbuddy.repository.UserRepository;

import java.util.List;
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/admin")
@Controller
public class AdminController {
	@Autowired
	private UserRepository userRepository;

	@GetMapping({"", "/", "/index"})
	public String adminPage(Model model) {
	    int page = 0; // trang đầu tiên
	    int size = 10; // số lượng người dùng mỗi trang

	    Pageable pageable = PageRequest.of(page, size);
	    Page<User> usersPage = userRepository.findAllByOrderByLastLoginDesc(pageable);

	    if (usersPage != null) {
	        List<User> userList = usersPage.getContent();
	        model.addAttribute("usersPage", userList);
	    } else {
	        model.addAttribute("errorMessage", "No users found.");
	    }

	    return "admin/index";
	}


}