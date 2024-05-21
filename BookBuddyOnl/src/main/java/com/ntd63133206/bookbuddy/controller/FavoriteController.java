package com.ntd63133206.bookbuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.ntd63133206.bookbuddy.model.Book;
import com.ntd63133206.bookbuddy.model.CustomUserDetails;
import com.ntd63133206.bookbuddy.model.Favorite;
import com.ntd63133206.bookbuddy.service.FavoriteService;

import java.util.List;

@Controller
@RequestMapping("/favorites")
public class FavoriteController {

	@Autowired
    private FavoriteService favoriteService;
	
    @PostMapping("/{bookId}")
    public String toggleFavoriteStatus(@PathVariable("bookId") Long bookId,
                                       @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                       @RequestParam("redirectUrl") String redirectUrl) {
        if (customUserDetails != null) {
            favoriteService.toggleFavorite(bookId, customUserDetails.getUsername());
        }
        return "redirect:" + redirectUrl;
    }
}
