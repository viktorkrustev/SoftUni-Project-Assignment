package com.onlineshop.controller;

import com.onlineshop.model.dto.LoginDTO;
import com.onlineshop.model.dto.RegisterDTO;
import com.onlineshop.model.dto.UserViewDTO;
import com.onlineshop.model.entity.Order;
import com.onlineshop.model.entity.User;
import com.onlineshop.service.OrderService;
import com.onlineshop.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Controller
public class UserController {
    private final UserService userService;
    private final OrderService orderService;


    public UserController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("/users/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerDTO", new RegisterDTO());
        return "register";
    }

    @PostMapping("/users/register")
    public String registerUser(@Valid @ModelAttribute("registerDTO") RegisterDTO registerDTO, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }

        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            model.addAttribute("passwordError", "Passwords do not match");
            return "register";
        }

        if (userService.existsByUsername(registerDTO.getUsername())) {
            model.addAttribute("registrationError", "Username already exists");
            return "register";
        }

        userService.save(registerDTO);
        return "redirect:/users/login";
    }

    @GetMapping("/users/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginDTO", new LoginDTO());
        return "login";
    }

    @Transactional
    @GetMapping("/users/profile")
    public String getProfile(Model model) {
        UserViewDTO userViewDTO = userService.getCurrentUserProfile();
        List<Order> orders = orderService.getOrdersForCurrentUser();
        model.addAttribute("user", userViewDTO);
        model.addAttribute("orders", orders);
        return "profile";
    }

    @PostMapping("/users/uploadProfilePicture")
    public String uploadProfilePicture(@RequestParam("profilePicture") MultipartFile profilePicture, Model model) {
        if (!profilePicture.isEmpty()) {
            try {
                userService.uploadProfilePicture(profilePicture);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "redirect:/users/profile";
    }

    @PostMapping("/users/updateProfile")
    public String updateProfile(@RequestParam("first_name") String firstName,
                                @RequestParam("last_name") String lastName,
                                @RequestParam("email") String email,
                                @RequestParam("username") String username,
                                RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserProfile(firstName, lastName, email, username);
            redirectAttributes.addFlashAttribute("message", "Profile updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating profile");
        }
        return "redirect:/users/profile";
    }

}
