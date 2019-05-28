package pl.simplemethod.codebin.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/404")
    public String errorPage() {
        return "404";
    }

    @GetMapping("/home")
    public String dashboard() {
        return "dashboard";
    }
}
