package com.onlineshop.service;

import com.onlineshop.model.dto.RegisterDTO;
import com.onlineshop.model.dto.UserViewDTO;
import com.onlineshop.model.entity.Role;
import com.onlineshop.model.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    void save(RegisterDTO registerDTO);

    boolean existsByUsername(String username);

    User findByUsername(String username);

    void saveUser(User user);

    boolean isAdmin(Authentication authentication);

    User getCurrentUser();

    List<User> findUserByRole(Role role);

    User findUserByEmail(String email);

    UserViewDTO getCurrentUserProfile();

    void uploadProfilePicture(MultipartFile profilePicture) throws IOException;

    void updateUserProfile(String firstName, String lastName, String email, String username);
}
