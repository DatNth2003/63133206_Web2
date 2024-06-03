package com.ntd63133206.bookbuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ntd63133206.bookbuddy.model.Book;
import com.ntd63133206.bookbuddy.service.BookService;
import com.ntd63133206.bookbuddy.service.FavoriteService;


@Controller
public class HomeController {
	
	@Autowired
	private BookService bookService;
	
	@Autowired
    private FavoriteService favoriteService;

    @GetMapping("/")
    public String homePage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        List<Book> latestBooks = bookService.get10LatestBooks();
        List<List<Book>> partitionedBooks = partitionList(latestBooks, 4);

        Map<Long, Boolean> favoriteStatusMap = new HashMap<>();
        if (userDetails != null) {
            String username = userDetails.getUsername();
            for (Book book : latestBooks) {
                boolean isFavorite = favoriteService.isBookFavoriteForUser(book.getId(), username);
                favoriteStatusMap.put(book.getId(), isFavorite);
            }
        }
        model.addAttribute("partitionedBooks", partitionedBooks);
        model.addAttribute("favoriteStatusMap", favoriteStatusMap);
        return "/home/index";
    }
    private List<List<Book>> partitionList(List<Book> list, int size) {
        List<List<Book>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            partitions.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return partitions;
    }
}
