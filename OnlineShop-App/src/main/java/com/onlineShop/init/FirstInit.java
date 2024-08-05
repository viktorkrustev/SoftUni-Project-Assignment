package com.onlineshop.init;

import com.onlineshop.model.entity.Role;
import com.onlineshop.model.entity.User;
import com.onlineshop.service.impl.UserServiceImpl;
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
    private final UserServiceImpl userService;
    private final DataSource dataSource;

    @Autowired
    public FirstInit(PasswordEncoder passwordEncoder, UserServiceImpl userService, DataSource dataSource) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.dataSource = dataSource;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!userService.existsByUsername("Viktor")) {
            User user1 = new User();
            user1.setFirstName("Viktor");
            user1.setLastName("Krustev");
            user1.setEmail("viktorkrustev03@abv.bg");
            user1.setPassword(passwordEncoder.encode("123456"));
            user1.setRole(Role.ADMIN);
            user1.setUsername("Viktor");
            userService.saveUser(user1);

            User user2 = new User();
            user2.setFirstName("Georgi");
            user2.setLastName("Dimitrov");
            user2.setEmail("georgid@abv.bg");
            user2.setPassword(passwordEncoder.encode("111111"));
            user2.setRole(Role.USER);
            user2.setUsername("Georgi");
            userService.saveUser(user2);

            executeSqlScripts();
        }
    }

    private void executeSqlScripts() {
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        resourceDatabasePopulator.addScript(new ClassPathResource("data.sql"));
        resourceDatabasePopulator.execute(dataSource);
    }
}
