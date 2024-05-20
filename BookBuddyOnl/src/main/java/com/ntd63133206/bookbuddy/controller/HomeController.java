package com.ntd63133206.bookbuddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

import com.ntd63133206.bookbuddy.model.Book;
import com.ntd63133206.bookbuddy.service.BookService;


@Controller
public class HomeController {
	
	@Autowired
	private BookService bookService;
	
	@GetMapping("/")
    public String homePage(Model model) {
        List<Book> latestBooks = bookService.get10LatestBooks();
        model.addAttribute("latestBooks", latestBooks);
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
