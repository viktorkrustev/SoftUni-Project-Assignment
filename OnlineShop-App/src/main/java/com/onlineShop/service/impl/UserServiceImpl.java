package com.onlineShop.service.impl;

import com.onlineShop.event.UserRegistrationEvent;
import com.onlineShop.model.dto.RegisterDTO;
import com.onlineShop.model.dto.UserViewDTO;
import com.onlineShop.model.entity.Role;
import com.onlineShop.model.entity.User;
import com.onlineShop.repository.UserRepository;
import com.onlineShop.service.UserService;
import com.onlineShop.util.CustomUserDetails;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper, ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void save(RegisterDTO registerDTO) {
        User user = modelMapper.map(registerDTO, User.class);
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);

        eventPublisher.publishEvent(new UserRegistrationEvent(this, registerDTO.getEmail()));
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public boolean isAdmin(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();

        return userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
    }

    @Override
    public List<User> findUserByRole(Role role) {
        return userRepository.findByRole(role);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public UserViewDTO getCurrentUserProfile() {
        User user = getCurrentUser();
        UserViewDTO userViewDTO = modelMapper.map(user, UserViewDTO.class);

        if (user.getProfilePicture() != null) {
            String base64Image = Base64.getEncoder().encodeToString(user.getProfilePicture());
            userViewDTO.setProfilePicture(base64Image);
        }
        return userViewDTO;
    }

    @Override
    public void uploadProfilePicture(MultipartFile profilePicture) throws IOException {
        User user = getCurrentUser();
        user.setProfilePicture(profilePicture.getBytes());
        saveUser(user);
    }

    @Override
    public void updateUserProfile(String firstName, String lastName, String email, String username) {
        User user = getCurrentUser();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setUsername(username);
        saveUser(user);
    }

    @Override
    public boolean isEmailAlreadyRegistered(String email) {
        return userRepository.findByEmail(email).isPresent();
    }


}
