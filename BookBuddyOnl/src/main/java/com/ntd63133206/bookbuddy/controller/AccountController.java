package com.ntd63133206.bookbuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


import com.ntd63133206.bookbuddy.model.User;
import com.ntd63133206.bookbuddy.service.EmailService;
import com.ntd63133206.bookbuddy.service.AccountService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.UUID;

@RequestMapping("/account")
@Controller
public class AccountController {
	
	@Autowired
    private final AccountService accountService;
	
	@Autowired
    private final EmailService emailService;

	public AccountController(AccountService accountService, EmailService emailService) {
        this.accountService = accountService;
        this.emailService = emailService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "account/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") @Valid User user,
                               BindingResult bindingResult,
                               Model model,
                               HttpServletRequest request,
                               @RequestParam("avatarFile") MultipartFile avatarFile,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin đăng ký.");
            return "account/register";
        }
        if (accountService.existsByUsername(user.getUsername())) {
            model.addAttribute("errorMessage", "Tên người dùng đã tồn tại. Vui lòng chọn tên người dùng khác.");
            return "account/register";
        }
        if (!avatarFile.isEmpty()) {
            try {
            	String avatarPath = accountService.saveAvatar(avatarFile);

                user.setAvatar(avatarPath);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
                model.addAttribute("errorMessage", "Không thể tải lên avatar.");
                return "account/register";
            }
        }

        user.setRole("USER");

        if (accountService.registerUser(user)) {
        	model.addAttribute("s", "Email đã tồn tại. Vui lòng chọn email khác.");
            return "redirect:/account/login";
        } else {
            model.addAttribute("errorMessage", "Đã xảy ra lỗi trong quá trình đăng ký. Vui lòng thử lại sau.");
            return "account/register";
        }
    }
    
    @GetMapping("/login")
    public String showLoginForm(Model model, HttpServletRequest request) {
        System.out.println("User vào trang đăng nhập!");
        HttpSession session = request.getSession();
        if (session.getAttribute("loginSuccess") != null && (boolean) session.getAttribute("loginSuccess")) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            model.addAttribute("loggedInUser", auth.getName());

            System.out.println("Session sau login:");
            Enumeration<String> attributeNames = session.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                String attributeName = attributeNames.nextElement();
                Object attributeValue = session.getAttribute(attributeName);
                System.out.println(attributeName + ": " + attributeValue);
            }

            return "/account/loginSuccess";
        } else {
            return "account/login";
        }
    }


    @PostMapping("/login")
    public String loginUser(@RequestParam("email") String email,
                            @RequestParam("password") String password,
                            Model model,
                            HttpServletRequest request,
                            RedirectAttributes redirectAttributes) {
        System.out.println("Login: đang xử lý post!");
        System.out.println("Login: Đã nhận thông tin từ " + email);

        if (accountService.authenticateUser(email, password)) {
            System.out.println("Login: Đã xác thực!");
            User user = accountService.getUserByEmail(email);
            
            if (user != null) {
                model.addAttribute("successMessage", "Login successful!");
                setSessionAttributes(request, user);
                return "redirect:/";
            } else {
                System.out.println("Login: Người dùng không tồn tại!");
                redirectAttributes.addFlashAttribute("errorMessage", "Người dùng không tồn tại. Vui lòng đăng ký tài khoản mới.");
                return "redirect:/account/login";
            }
        } else {
            System.out.println("Login: Xác thực thất bại!");
            redirectAttributes.addFlashAttribute("errorMessage", "Email hoặc mật khẩu không đúng. Vui lòng thử lại.");
            return "redirect:/account/login";
        }
    }


    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "account/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        if (accountService.existsByEmail(email)) {
            System.out.println("/forgot-password: Email có tồn tại");

            try {
                String resetToken = accountService.generateResetToken(email);
                System.out.println("Tạo reset token: " + resetToken);

                String resetLink = "http://localhost:8080/account/reset-password?token=" + resetToken;
                System.out.println("Tạo reset link: " + resetToken);
                emailService.sendResetPasswordEmail(email, resetLink);

                model.addAttribute("successMessage", "Link đặt lại mật khẩu đã được gửi tới email của bạn.");
                System.out.println("Link đặt lại mật khẩu đã được gửi tới email của bạn.");
                return "redirect:/account/reset-password?token=" + resetToken;
            } catch (MessagingException e) {
                e.printStackTrace();
                System.out.println("Gửi email đặt lại mật khẩu thất bại: " + e.getMessage());
                model.addAttribute("errorMessage", "Có lỗi xảy ra khi gửi email đặt lại mật khẩu. Vui lòng thử lại sau.");
            }
        } else {
            System.out.println("Email không tồn tại trong hệ thống. Vui lòng kiểm tra lại.");
            model.addAttribute("errorMessage", "Email không tồn tại trong hệ thống. Vui lòng kiểm tra lại.");
        }
        System.out.println("/forgot-password: Email không tồn tại");

        return "account/forgot-password";
    }


    
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token, @RequestParam("newPassword") String newPassword, Model model) {
        if (accountService.resetPassword(token, newPassword)) {
            model.addAttribute("successMessage", "Đặt lại mật khẩu thành công!");
        } else {
            model.addAttribute("errorMessage", "Token không hợp lệ hoặc đã hết hạn.");
        }
        return "reset-password";
    }
    @GetMapping("/profile")
    public String showEditProfileForm(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        getSessionAttributes(request);

        if (session.getAttribute("loggedInUserEmail") == null) {
            return "redirect:/account/login";
        }

        String email = (String) session.getAttribute("loggedInUserEmail");
        
        User user = accountService.getUserByEmail(email);
        
        model.addAttribute("user", user);
        
        return "account/profile";
    }
    @PostMapping("/profile")
    public String updateProfile(@RequestParam("username") String username,
                                @RequestParam("avatarFile") MultipartFile avatarFile,
                                HttpServletRequest request,
                                RedirectAttributes redirectAttributes) {
        String email = (String) request.getSession().getAttribute("loggedInUserEmail");

        try {
            accountService.updateProfile(email, username, avatarFile);
            
            User updatedUser = accountService.getUserByEmail(email);
            request.getSession().setAttribute("user", updatedUser);
            setSessionAttributes(request, updatedUser);
            System.out.println("updateProfile");
            
            redirectAttributes.addFlashAttribute("successMessage", "Thông tin cá nhân đã được cập nhật thành công.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể cập nhật thông tin cá nhân. Vui lòng thử lại sau.");
        }
        
        return "redirect:/account/profile";
    }

    private void setSessionAttributes(HttpServletRequest request, User user) {
        HttpSession session = request.getSession();
        session.setAttribute("loginSuccess", true);
        session.setAttribute("loggedInUserEmail", user.getEmail());
        session.setAttribute("loggedInUsername", user.getUsername());
        String avatarPath = "/images/users/avatars/" + user.getAvatar();
        session.setAttribute("loggedInUserAvatar", avatarPath);
        
    }
    private void getSessionAttributes(HttpServletRequest request) {
    	HttpSession session = request.getSession();
    	Enumeration<String> attributeNames = session.getAttributeNames();
    	while (attributeNames.hasMoreElements()) {
    		String attributeName = attributeNames.nextElement();
    		Object attributeValue = session.getAttribute(attributeName);
    		System.out.println(attributeName + ": " + attributeValue);
    	}
    }
}
