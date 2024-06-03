package com.ntd63133206.bookbuddy.controller;

import com.ntd63133206.bookbuddy.model.Role;
import com.ntd63133206.bookbuddy.model.User;
import com.ntd63133206.bookbuddy.repository.UserRepository;
import com.ntd63133206.bookbuddy.service.UserService;
import com.ntd63133206.bookbuddy.service.RoleService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.HttpStatus;

@Controller
@RequestMapping("/admin/users")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private RoleService roleService;
    
    @GetMapping("/")
    public String viewHomePage(Model model,
                               @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
                               @RequestParam(name = "role", required = false, defaultValue = "") String role,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size) {
        Page<User> usersPage;
        PageRequest pageRequest = PageRequest.of(page, size);

        if ("".equals(keyword)) {
            keyword = null;
        }

        Long roleIdLong = null;
        if (!"".equals(role)) {
            Role roleObject = roleService.getRoleByName(role);
            if (roleObject != null) {
                roleIdLong = roleObject.getId();
            }
        }

        if ("".equals(role)) {
            usersPage = userService.searchUsersByKeyword(keyword, pageRequest);
        } else {
            usersPage = userService.searchUsersByKeywordAndRole(keyword, roleIdLong, pageRequest);
        }

        model.addAttribute("users", usersPage.getContent());
        model.addAttribute("currentPage", usersPage.getNumber() + 1);
        model.addAttribute("totalPages", usersPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("roles", roleService.getAllRoles());

        return "admin/users/user-list";
    }

    @PostMapping("/search")
    public String searchUsers(@RequestParam(name = "keyword", required = false) String keyword,
                              @RequestParam(name = "role", required = false) String role,
                              RedirectAttributes redirectAttributes) {
        return "redirect:/admin/users/?keyword=" + keyword + "&role=" + role;
    }



    @GetMapping("/add")
    public String showUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.getAllRoles());
        return "admin/users/add-user";
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute("user") User user,
                          @RequestParam("avatarFile") MultipartFile avatarFile,
                          @RequestParam("selectedRoles") List<Long> selectedRoleIds,
                          RedirectAttributes redirectAttributes) {
        try {
            Set<Role> selectedRoles = selectedRoleIds.stream()
                    .map(roleId -> roleService.getRoleById(roleId).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            user.setRoles(selectedRoles);

            if (avatarFile != null && !avatarFile.isEmpty()) {
                String avatarPath = userService.saveAvatar(avatarFile);
                user.setAvatar(avatarPath);
            } else {
                String defaultAvatarPath = "/default/default-avatar.jpg";
                user.setAvatar(defaultAvatarPath);
            }
            
            userService.save(user);

            redirectAttributes.addFlashAttribute("successMessage", "Đã thêm người dùng thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/users/";
    }



    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        try {
            User user = userService.getUserById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng có ID: " + id));
            model.addAttribute("user", user);
            model.addAttribute("roles", roleService.getAllRoles());
            return "admin/users/edit-user";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "admin/users";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable("id") Long id,
                             @ModelAttribute("user") User user,
                             @RequestParam List<Long> selectedRoles,
                             Model model) {
        try {
            User currentUser = userService.getUserById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng có ID: " + id));

            currentUser.setEmail(user.getEmail());
            currentUser.setUsername(user.getUsername());

            Set<Role> roles = selectedRoles.stream()
                    .map(roleId -> roleService.getRoleById(roleId).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            currentUser.setRoles(roles);

            userService.save(currentUser);

            model.addAttribute("successMessage", "Cập nhật người dùng thành công.");
            model.addAttribute("user", currentUser);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "admin/users/edit-user";
    }




    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") User user, Model model) {
        try {
            userService.save(user);
            model.addAttribute("successMessage", "Đã lưu người dùng thành công.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/users/";
    }
    


    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, Model model) {
        try {
            userService.deleteUserById(id);
            model.addAttribute("successMessage", "Đã xóa người dùng thành công.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/users/";
    }
}
