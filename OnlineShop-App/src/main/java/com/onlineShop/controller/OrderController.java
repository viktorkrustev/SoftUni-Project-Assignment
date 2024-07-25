package com.onlineshop.controller;

import com.onlineshop.model.dto.OrderDTO;
import com.onlineshop.model.dto.ProductsDTO;
import com.onlineshop.model.entity.User;
import com.onlineshop.service.CartService;
import com.onlineshop.service.OrderService;
import com.onlineshop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class OrderController {

    private final UserService userService;
    private final CartService cartService;
    private final OrderService orderService;

    public OrderController(UserService userService, CartService cartService, OrderService orderService) {
        this.userService = userService;
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @GetMapping("/checkout")
    public String checkout(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        List<ProductsDTO> cartItems = cartService.getCartItemsForUser(username);
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }
        double totalPrice = cartItems.stream()
                .mapToDouble(ProductsDTO::getPrice)
                .sum();

        OrderDTO orderDTO = new OrderDTO();
        model.addAttribute("user", user);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", String.format("%.2f", totalPrice));
        model.addAttribute("orderDTO", orderDTO);

        return "checkout";
    }

    @PostMapping("/confirm-order")
    public String confirmOrder(@Valid @ModelAttribute("orderDTO") OrderDTO orderDTO, BindingResult result, Model model) {
        if (result.hasErrors()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.findByUsername(username);

            List<ProductsDTO> cartItems = cartService.getCartItemsForUser(username);
            double totalPrice = cartItems.stream()
                    .mapToDouble(ProductsDTO::getPrice)
                    .sum();

            model.addAttribute("user", user);
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("totalPrice", String.format("%.2f", totalPrice));
            model.addAttribute("orderDTO", orderDTO);

            return "checkout";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        List<ProductsDTO> cartItems = cartService.getCartItemsForUser(username);
        double totalPrice = cartItems.stream()
                .mapToDouble(ProductsDTO::getPrice)
                .sum();

        orderService.createOrder(user, cartItems, totalPrice, orderDTO);
        cartService.deleteAllProductsFromCart(user.getCart().getId());

        return "redirect:/cart";
    }

}
