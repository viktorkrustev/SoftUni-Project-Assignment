package com.onlineShop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String showHomePage(Model model) {
        model.addAttribute("title", "Welcome to My Online Store");
        model.addAttribute("description", "Explore the Latest in Electronics");
        return "index";
    }

    @GetMapping("/about")
    public String showAboutPage(Model model) {
        model.addAttribute("title", "About Us");
        model.addAttribute("description", "Learn more about our online store and what we offer.");
        return "about";
    }
}
