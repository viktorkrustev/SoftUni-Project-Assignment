package com.onlineShop.controller;

import com.onlineShop.model.dto.ProductsDTO;
import com.onlineShop.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final ProductService productService;

    public HomeController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String showHomePage(Model model) {
        model.addAttribute("title", "Welcome to My Online Store");
        model.addAttribute("description", "Explore the Latest in Electronics");
        List<ProductsDTO> products = productService.getAllProducts();

        // Разбъркване на продуктите
        Collections.shuffle(products);

        // Добавяне на избраните продукти към модела
        model.addAttribute("products", products);
        return "index";
    }

    @GetMapping("/about")
    public String showAboutPage(Model model) {
        model.addAttribute("title", "About Us");
        model.addAttribute("description", "Learn more about our online store and what we offer.");
        return "about";
    }

}
