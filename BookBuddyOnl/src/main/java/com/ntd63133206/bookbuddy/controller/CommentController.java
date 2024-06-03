package com.ntd63133206.bookbuddy.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ntd63133206.bookbuddy.model.User;
import com.ntd63133206.bookbuddy.service.CommentService;
import com.ntd63133206.bookbuddy.service.UserService;

@Controller
@RequestMapping("/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;
    
    @PostMapping("/add/{bookId}")
    public String addComment(@PathVariable Long bookId,
                             @RequestParam("content") String content,
                             @AuthenticationPrincipal User user,
                             RedirectAttributes redirectAttributes) {
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn cần đăng nhập để thực hiện hành động này!");
            return "redirect:/books/details/" + bookId;
        }

        if (content == null || content.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nội dung bình luận trống!");
            return "redirect:/books/details/" + bookId;
        }

        Long userId = user.getId();
        commentService.add(bookId, userId, content);

        return "redirect:/books/details/" + bookId;
    }

}
