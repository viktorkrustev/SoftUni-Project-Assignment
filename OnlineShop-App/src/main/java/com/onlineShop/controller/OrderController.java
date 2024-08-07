package com.onlineShop.controller;

import com.onlineShop.model.dto.OrderDTO;
import com.onlineShop.model.dto.ProductsDTO;
import com.onlineShop.model.entity.User;
import com.onlineShop.service.CartService;
import com.onlineShop.service.OrderService;
import com.onlineShop.service.UserService;
import jakarta.validation.Valid;
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
        User user = userService.getCurrentUser();

        List<ProductsDTO> cartItems = cartService.getCartItemsForUser(user.getUsername());
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
        User user = userService.getCurrentUser();
        List<ProductsDTO> cartItems = cartService.getCartItemsForUser(user.getUsername());
        double totalPrice = cartItems.stream()
                .mapToDouble(ProductsDTO::getPrice)
                .sum();

        model.addAttribute("user", user);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", String.format("%.2f", totalPrice));
        model.addAttribute("orderDTO", orderDTO);

        if (result.hasErrors()) {
            return "checkout";
        }

        orderService.createOrder(user, cartItems, totalPrice, orderDTO);
        cartService.deleteAllProductsFromCart(user.getCart().getId());

        return "redirect:/cart";
    }


}