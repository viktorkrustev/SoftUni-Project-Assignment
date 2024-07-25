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

        // Запис на новия потребител
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
        User user = userService.getCurrentUser();
        List<Order> orders = orderService.getOrdersByUser(user);

        // Преобразуване на User в UserViewDTO
        UserViewDTO userViewDTO = new UserViewDTO();
        userViewDTO.setEmail(user.getEmail());
        userViewDTO.setFirstName(user.getFirstName());
        userViewDTO.setLastName(user.getLastName());
        userViewDTO.setUsername(user.getUsername());

        // Добавяне на Base64 картината
        if (user.getProfilePicture() != null) {
            String base64Image = Base64.getEncoder().encodeToString(user.getProfilePicture());
            userViewDTO.setProfilePicture(base64Image);
        }

        model.addAttribute("user", userViewDTO);
        model.addAttribute("orders", orders);
        return "profile";
    }


    @PostMapping("/users/uploadProfilePicture")
    public String uploadProfilePicture(@RequestParam("profilePicture") MultipartFile profilePicture,
                                       @AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (!profilePicture.isEmpty()) {
            try {
                User user = userService.findByUsername(userDetails.getUsername());
                user.setProfilePicture(profilePicture.getBytes());
                userService.saveUser(user);

                model.addAttribute("user", user);
                if (user.getProfilePicture() != null) {
                    String base64Image = Base64.getEncoder().encodeToString(user.getProfilePicture());
                    model.addAttribute("profilePicture", base64Image);
                }

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
            User user = userService.getCurrentUser();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setUsername(username);
            userService.saveUser(user);
            redirectAttributes.addFlashAttribute("message", "Profile updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating profile");
        }

        return "redirect:/users/profile";
    }


}
