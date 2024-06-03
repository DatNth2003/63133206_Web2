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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ntd63133206.bookbuddy.model.User;
import com.ntd63133206.bookbuddy.model.Role;
import com.ntd63133206.bookbuddy.service.EmailService;
import com.ntd63133206.bookbuddy.service.UserService;
import com.ntd63133206.bookbuddy.util.EmailUtils;
import com.ntd63133206.bookbuddy.util.Utility;

import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;


import java.io.File;
import java.io.IOException;

@RequestMapping("/account")
@Controller
public class AccountController {
    
	@Autowired
	private UserService userService;

	@Autowired
    private JavaMailSender mailSender;
	@Autowired
    private AuthenticationManager authenticationManager;

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
                String defaultAvatarPath = "default/default-avatar.jpg";
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
    public String login(Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        System.out.println("Email: " + email);
        System.out.println("Password: " + password);

        Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);

        try {
            Authentication result = authenticationManager.authenticate(authentication);
            SecurityContextHolder.getContext().setAuthentication(result);
            System.out.println("Authentication result: " + result.toString());

            boolean isAdmin = result.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

            if (isAdmin) {
                return "redirect:/admin/";
            } else {
                return "redirect:/";
            }
        } catch (AuthenticationException e) {
            System.out.println(e.getMessage());
            return "account/login";
        }
    }



    @GetMapping("/profile")
    public String showEditProfileForm(Model model, @AuthenticationPrincipal User currentUser) {
        User user = currentUser;
        model.addAttribute("user", user);
        return "account/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute("user") User user, @RequestParam("avatarFile") MultipartFile avatarFile, RedirectAttributes redirectAttributes) {
        try {
            userService.updateProfile(user.getUsername(), user.getUsername(), avatarFile);
            redirectAttributes.addFlashAttribute("successMessage", "Thông tin cá nhân đã được cập nhật thành công.");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể cập nhật thông tin cá nhân. Vui lòng thử lại sau.");
        }
        return "redirect:/account/profile";
    }



//    @GetMapping("/forgot-password")
//    public String showForgotPasswordForm() {
//        return "account/forgot-password";
//    }
//
//    @PostMapping("/forgot-password")
//    public String processForgotPassword(HttpServletRequest request, Model model) {
//        String email = request.getParameter("email");
//        String token = RandomString.make(30);
//
//        try {
//            if (userService.existsByEmail(email)) {
//                userService.updateResetPasswordToken(token, email);
//                String resetPasswordLink = Utility.getSiteURL(request) + "/reset-password?token=" + token;
//                if (sendEmail(email, resetPasswordLink)) {
//                    model.addAttribute("message", "We have sent a reset password link to your email. Please check.");
//                } else {
//                    model.addAttribute("error", "Failed to send reset password email.");
//                }
//            } else {
//                model.addAttribute("error", "Email address not found.");
//            }
//        } catch (Exception e) {
//            model.addAttribute("error", e.getMessage());
//        }
//
//        return "redirect:/account/forgot-password";
//    }


//    public boolean sendEmail(String recipientEmail, String link) {
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//            Properties properties = new Properties();
//            properties.put("mail.smtp.auth", "true");
//            properties.put("mail.smtp.starttls.enable", "true");
//            properties.put("mail.smtp.host", "smtp.gmail.com");
//            properties.put("mail.smtp.port", "587");
//            properties.put("mail.smtp.username", "bookbuddy@gmail.com");
//            properties.put("mail.smtp.password", "NgoThanhDat2003");
//
//            Authenticator authenticator = new Authenticator() {
//                @Override
//                protected PasswordAuthentication getPasswordAuthentication() {
//                    return new PasswordAuthentication("bookbuddy@gmail.com", "NgoThanhDat2003");
//                }
//            };
//
//            Session session = Session.getInstance(properties, authenticator);
//
//            helper.setFrom("bookbuddy@gmail.com", "BookBuddy Support");
//            helper.setTo(recipientEmail);
//            String subject = "Here's the link to reset your password";
//            String content = EmailUtils.getEmailMessage("", link);
//            helper.setSubject(subject);
//            helper.setText(content, true);
//
//            Transport.send(message);
//
//            return true;
//        } catch (Exception e) {
//            System.out.println("Failed to send email: " + e.getMessage());
//            return false;
//        }
//    }



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
    public String logout(HttpServletRequest request) {
    	System.out.println("Logout!");
        return "redirect:/account/login";
    }

}

