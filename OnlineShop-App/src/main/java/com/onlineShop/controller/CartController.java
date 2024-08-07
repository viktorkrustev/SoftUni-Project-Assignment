package com.onlineShop.controller;

import com.onlineShop.model.dto.ProductsDTO;
import com.onlineShop.model.entity.User;
import com.onlineShop.service.CartService;
import com.onlineShop.service.ProductService;
import com.onlineShop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CartController {

    private final CartService cartService;
    private final ProductService productService;
    private final UserService userService;

    @Autowired
    public CartController(CartService cartService, ProductService productService, UserService userService) {
        this.cartService = cartService;
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping("/cart")
    public String viewCart(Model model) {
        User user = userService.getCurrentUser();

        List<ProductsDTO> cartItems = cartService.getCartItemsForUser(user.getUsername());

        double totalPrice = cartItems.stream()
                .mapToDouble(ProductsDTO::getPrice)
                .sum();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);

        return "cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("productId") Long productId, RedirectAttributes redirectAttributes) {
        boolean addedToCart = productService.addToCart(productId);
        if (!addedToCart) {
            redirectAttributes.addFlashAttribute("error", "Sorry, this product is currently out of stock.");
            return "redirect:/product-unavailable";
        }
        return "redirect:/products";
    }

    @DeleteMapping("/cart/remove/{productId}")
    public String removeProductFromCart(@PathVariable Long productId) {
        cartService.removeProductFromCart(productId);
        return "redirect:/cart";
    }

}