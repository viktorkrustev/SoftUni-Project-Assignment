package com.onlineShop.repository;

import com.onlineShop.model.entity.Role;
import com.onlineShop.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    List<User> findByRole(Role role);

    Optional<User> findByEmail(String to);
}
