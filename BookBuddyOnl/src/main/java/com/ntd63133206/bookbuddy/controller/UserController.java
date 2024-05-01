package com.ntd63133206.bookbuddy.controller;

import com.ntd63133206.bookbuddy.model.User;
import com.ntd63133206.bookbuddy.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/users")
public class UserController {

    @Autowired
    private AccountService accountService;

    @GetMapping(value = {"/", "/user-list"})
    public String listUsers(Model model) {
        model.addAttribute("users", accountService.getAllUsers());
        return "admin/users/user-list";
    }

    @GetMapping("/add-user")
    public String showUserForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/users/form";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") User user, Model model) {
        try {
            accountService.saveUser(user);
            model.addAttribute("successMessage", "Đã lưu người dùng thành công.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/users/";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, Model model) {
        try {
            accountService.deleteUserById(id);
            model.addAttribute("successMessage", "Đã xóa người dùng thành công.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/users/";
    }
}
