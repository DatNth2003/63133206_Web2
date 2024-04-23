package com.ntd63133206.bookbuddy.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.ntd63133206.bookbuddy.model.User;
import com.ntd63133206.bookbuddy.service.UserService;

import jakarta.validation.Valid;

@Controller
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/userLogin")
    public String loginUser(@ModelAttribute("user") User user) {
        if (user.getEmail() == null || user.getPassword() == null) {
            return "error";
        }
        
        Optional<User> userDataOptional = userService.findByEmail(user.getEmail());
        
        if (userDataOptional.isPresent()) {
            User userData = userDataOptional.get();
            
            if (passwordEncoder.matches(user.getPassword(), userData.getPassword())) {
                return "home"; // Đăng nhập thành công
            } else {
                return "error"; // Sai mật khẩu
            }
        } else {
            return "error"; // Email không tồn tại
        }
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/userRegister")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }

        if (userService.isEmailExist(user.getEmail())) {
            model.addAttribute("error", "Email đã tồn tại");
            return "register";
        }

        userService.saveUser(user);
        
        model.addAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
        return "login";
    }

    
    @GetMapping("/users")
    public String getAllUsers(Model model) {
        List<User> userList = userService.getAllUsers(); 
        model.addAttribute("users", userList);
        return "userList";
    }
}
