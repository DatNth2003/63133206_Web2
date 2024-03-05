package com.ntd63133206.sprintmvc;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {

    @GetMapping("/")
    public String showHelloPage() {
        return "say-hello";
    }
    @GetMapping("/hello")
    public String getHello(@RequestParam("strHello") String strHello, Model model) {
        return "say-custom-hello";
        model.addAttribute("strHello", strHello);

    }
    @GetMapping("/about")
    public String about() {
        return "about";
    }
}

