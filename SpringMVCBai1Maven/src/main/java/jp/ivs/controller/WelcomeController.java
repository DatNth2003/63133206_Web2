package jp.ivs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WelcomeController {
	@RequestMapping("/welcome")
	public String welcome(ModelMap model) {
		model.addAttribute("msg", "Helo maven");
		return "viewWelcome";
	}
}
