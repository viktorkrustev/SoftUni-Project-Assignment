package com.onlineshop.init;

import com.onlineshop.model.entity.Role;
import com.onlineshop.model.entity.User;
import com.onlineshop.repository.ProductRepository;
import com.onlineshop.repository.UserRepository;
import com.onlineshop.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class FirstInit implements CommandLineRunner {
    private final PasswordEncoder passwordEncoder;

    private final UserService userService;

    public FirstInit(PasswordEncoder passwordEncoder, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }


    @Override
    @Transactional
    public void run(String... args) {
        if (!userService.existsByUsername("Viktor")){
            User user = new User();
            user.setFirstName("Viktor");
            user.setLastName("Krustev");
            user.setEmail("viktorkrustev03@abv.bg");
            user.setPassword(passwordEncoder.encode("123456"));
            user.setRole(Role.ADMIN);
            user.setUsername("Viktor");
            userService.saveUser(user);

        }
    }
}
