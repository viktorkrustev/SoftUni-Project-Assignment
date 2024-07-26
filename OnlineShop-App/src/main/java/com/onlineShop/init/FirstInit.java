package com.onlineshop.init;

import com.onlineshop.model.entity.Role;
import com.onlineshop.model.entity.User;
import com.onlineshop.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class FirstInit implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final DataSource dataSource;

    @Autowired
    public FirstInit(PasswordEncoder passwordEncoder, UserService userService, DataSource dataSource) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.dataSource = dataSource;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!userService.existsByUsername("Viktor")) {
            User user = new User();
            user.setFirstName("Viktor");
            user.setLastName("Krustev");
            user.setEmail("viktorkrustev03@abv.bg");
            user.setPassword(passwordEncoder.encode("123456"));
            user.setRole(Role.ADMIN);
            user.setUsername("Viktor");
            userService.saveUser(user);
            executeSqlScripts();
        }
    }

    private void executeSqlScripts() {
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        resourceDatabasePopulator.addScript(new ClassPathResource("data.sql"));
        resourceDatabasePopulator.execute(dataSource);
    }
}
