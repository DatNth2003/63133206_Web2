package com.ntd63133206.bookbuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import com.ntd63133206.bookbuddy.model.User;
import com.ntd63133206.bookbuddy.model.CustomUserDetails;
import com.ntd63133206.bookbuddy.model.Role;
import com.ntd63133206.bookbuddy.service.CustomUserDetailsService;
import com.ntd63133206.bookbuddy.service.EmailService;
import com.ntd63133206.bookbuddy.service.UserService;
import com.ntd63133206.bookbuddy.util.Utility;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.UUID;
import java.util.Set;
import net.bytebuddy.utility.RandomString;

@RequestMapping("/account")
@Controller
public class AccountController {
    
	@Autowired
	private CustomUserDetailsService customUserDetailsService;
	private final UserService userService;

	public AccountController(CustomUserDetailsService customUserDetailsService, UserService userService) {
	    this.customUserDetailsService = customUserDetailsService;
	    this.userService = userService;
	}

	@Autowired
    private JavaMailSender mailSender;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "account/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user,
                               BindingResult bindingResult,
                               @RequestParam("avatarFile") MultipartFile avatarFile,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin đăng ký.");
            return "redirect:/account/register";
        }

        if (userService.existsByUsername(user.getUsername())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tên người dùng đã tồn tại. Vui lòng chọn tên người dùng khác.");
            return "redirect:/account/register";
        }

        try {
        	if (avatarFile == null || avatarFile.isEmpty()) {
                String defaultAvatarPath = "/images/default/default-avatar.jpg";
                user.setAvatar(defaultAvatarPath);
            }
            userService.registerUser(user, avatarFile);
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký thành công!");
            return "redirect:/account/login";
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi trong quá trình đăng ký. Vui lòng thử lại sau.");
            return "redirect:/account/register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "account/loginSuccess";
        }
        return "/account/login";
    }


    @PostMapping("/login")
    public String login(@ModelAttribute User user, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        CustomUserDetails customUserDetails;
        try {
            customUserDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(user.getUsername());
            if (customUserDetails != null) {
                System.out.println("CustomUserDetails: " + customUserDetails.getAvatar());
                model.addAttribute("authentication", customUserDetails);

                boolean isAdmin = customUserDetails.getAuthorities().stream()
                        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

                if (isAdmin) {
                    return "redirect:/admin/";
                } else {
                    return "redirect:/";
                }
            } else {
                System.out.println("CustomUserDetails is null!");
                model.addAttribute("errorMessage", "Không thể đăng nhập. Vui lòng thử lại sau.");
                return "redirect:/account/login?error";
            }
        } catch (UsernameNotFoundException e) {
            System.out.println("Tên người dùng không tồn tại.");

            model.addAttribute("errorMessage", "Tên người dùng không tồn tại.");
            return "redirect:/account/login?error";
        }
    }






    @GetMapping("/profile")
    public String showEditProfileForm(Model model, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String username = customUserDetails.getUsername();
        System.out.println("Username retrieved from CustomUserDetails: " + username);

        User user = customUserDetails.getUser();
        if (user != null) {
            System.out.println("User retrieved successfully: " + user.toString());
        } else {
            System.out.println("User not found for username: " + username);
        }

        model.addAttribute("user", user);
        return "account/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam("username") String username,
                                @RequestParam("avatarFile") MultipartFile avatarFile,
                                @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                RedirectAttributes redirectAttributes) {
        try {
            userService.updateProfile(customUserDetails.getUsername(), username, avatarFile);
            redirectAttributes.addFlashAttribute("successMessage", "Thông tin cá nhân đã được cập nhật thành công.");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể cập nhật thông tin cá nhân. Vui lòng thử lại sau.");
        }

        return "redirect:/account/profile";
    }



    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "account/forgot-password";
    }


    @PostMapping("/forgot-password")
    public String processForgotPassword(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        String token = RandomString.make(30);

        try {
            if (userService.existsByEmail(email)) {
                userService.updateResetPasswordToken(token, email);
                String resetPasswordLink = Utility.getSiteURL(request) + "/reset-password?token=" + token;
                // Gửi email và kiểm tra kết quả
                if (sendEmail(email, resetPasswordLink)) {
                    model.addAttribute("message", "We have sent a reset password link to your email. Please check.");
                } else {
                    model.addAttribute("error", "Failed to send reset password email.");
                }
            } else {
                model.addAttribute("error", "Email address not found.");
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "redirect:/account/forgot-password";
    }


    public boolean sendEmail(String recipientEmail, String link) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("bookbuddy@gmail.com", "BookBuddy Support");
            helper.setTo(recipientEmail);

            String subject = "Here's the link to reset your password";

            String content = "<p>Hello,</p>"
                    + "<p>You have requested to reset your password.</p>"
                    + "<p>Click the link below to change your password:</p>"
                    + "<p><a href=\"" + link + "\">Change my password</a></p>"
                    + "<br>"
                    + "<p>Ignore this email if you do remember your password, or you have not made the request.</p>";

            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);

            // Gửi email thành công
            return true;
        } catch (Exception e) {
            // Gửi email thất bại, ghi log hoặc xử lý lỗi tại đây
            System.out.println("Failed to send email: " + e.getMessage());
            return false;
        }
    }


	@GetMapping("/reset-password")
	public String showResetPasswordForm(@Param(value = "token") String token, Model model) {
    User user = userService.getByResetPasswordToken(token);
    model.addAttribute("token", token);
     
    if (user == null) {
        model.addAttribute("message", "Invalid Token");
        return "message";
    }
     
    return "resetPassword";
	}

	@PostMapping("/reset-password")
	public String processResetPassword(HttpServletRequest request, Model model) {
		String token = request.getParameter("token");
		String password = request.getParameter("password");
		
		User user = userService.getByResetPasswordToken(token);
		model.addAttribute("title", "Reset your password");
		
		if (user == null) {
			model.addAttribute("message", "Invalid Token");
			return "resetPassword";
		} else {           
			userService.updatePassword(user, password);
			
			model.addAttribute("message", "You have successfully changed your password.");
		}
		
		return "redirect:/account/login";
	}
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {        System.out.println("Logout!");
        return "redirect:/account/login";
    }

}

