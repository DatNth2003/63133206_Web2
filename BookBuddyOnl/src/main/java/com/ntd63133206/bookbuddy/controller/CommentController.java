package com.ntd63133206.bookbuddy.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ntd63133206.bookbuddy.model.Comment;
import com.ntd63133206.bookbuddy.model.User;
import com.ntd63133206.bookbuddy.service.CommentService;
import com.ntd63133206.bookbuddy.service.UserService;

@RequestMapping("/comments")
public class CommentController {
	@Autowired
	public CommentService commentService;

	@Autowired
    private UserService userService;
	
	@PostMapping("/comments/{bookId}")
	public String addComment(@PathVariable Long bookId, @ModelAttribute("comment") Comment comment, Principal principal) {
	    if (comment == null || comment.getContent() == null || comment.getContent().isEmpty()) {
	        return "redirect:/details/" + bookId;
	    }

	    if (principal == null) {
	        return "redirect:/books/details/" + bookId;
	    }
	    
	    String username = principal.getName();
	    User user = userService.getUserByUsername(username);
	    Long userId = user.getId();

	    commentService.addComment(bookId, userId, comment.getContent());

	    return "redirect:/books/details/" + bookId;
	}


}
