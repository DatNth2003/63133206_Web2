package com.ntd63133206.bookbuddy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import com.ntd63133206.bookbuddy.model.Role;
import com.ntd63133206.bookbuddy.model.User;

import jakarta.servlet.http.HttpSession;

@RequestMapping("/admin")
@Controller
public class AdminController {
	@GetMapping({"", "/", "/index"})
    public String adminPage(HttpSession session) {
		User loggedInUser = (User) session.getAttribute("loggedInUser");
	    if (loggedInUser != null) {
	        if (checkAdminRole(loggedInUser)) {
	            System.out.println("Admin: Xác thực thành công cho " + loggedInUser.getUsername());
	            return "admin/index";
	        } else {
	            System.out.println("Admin: Xác thực thành công thất bại!");
	            return "error/access-denied";
	        }
	    } else {
	    	return "error/access-denied";
	    }
    }
	private boolean checkAdminRole(User user) {
        for (Role role : user.getRoles()) {
            if ("ADMIN".equals(role.getName())) {
                return true;
            }
        }
        return false;
    }
}
