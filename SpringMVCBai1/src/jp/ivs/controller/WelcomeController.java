package jp.ivs.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WelcomeController {
	@RequestMapping("welcome")
	public String welcome(HttpServletRequest req) {
		req.setAttribute("msg", "He lo");
		return "viewWelcome";
	}
}
