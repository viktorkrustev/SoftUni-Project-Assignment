package com.onlineShop.service;

import com.onlineShop.model.dto.UserViewDTO;
import com.onlineShop.model.entity.User;
import com.onlineShop.repository.UserRepository;
import com.onlineShop.service.impl.UserServiceImpl;
import com.onlineShop.util.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void existsByUsername_shouldReturnTrue_whenUserExists() {
        when(userRepository.existsByUsername("testUser")).thenReturn(true);

        boolean exists = userService.existsByUsername("testUser");

        assertTrue(exists);
    }

    @Test
    void existsByUsername_shouldReturnFalse_whenUserDoesNotExist() {
        when(userRepository.existsByUsername("testUser")).thenReturn(false);

        boolean exists = userService.existsByUsername("testUser");

        assertFalse(exists);
    }

    @Test
    void findByUsername_shouldReturnUser_whenUserExists() {
        User user = new User();
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        User foundUser = userService.findByUsername("testUser");

        assertEquals(user, foundUser);
    }

    @Test
    void findByUsername_shouldReturnNull_whenUserDoesNotExist() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        User foundUser = userService.findByUsername("testUser");

        assertNull(foundUser);
    }

    @Test
    void saveUser_shouldSaveUser() {
        User user = new User();
        userService.saveUser(user);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void getCurrentUser_shouldReturnAuthenticatedUser() {
        User user = new User();
        user.setId(1L);
        CustomUserDetails userDetails = new CustomUserDetails(1L, "testUser", "password", List.of(() -> "ROLE_USER"));
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User currentUser = userService.getCurrentUser();

        assertEquals(user, currentUser);
    }

    @Test
    void getCurrentUser_shouldThrowException_whenUserNotAuthenticated() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);
        SecurityContextHolder.setContext(new SecurityContextImpl(authentication));

        Exception exception = assertThrows(RuntimeException.class, () -> userService.getCurrentUser());

        assertEquals("No authenticated user found", exception.getMessage());
    }

    @Test
    void getCurrentUserProfile_shouldReturnUserProfile() {
        User user = new User();
        user.setId(1L);
        user.setProfilePicture(new byte[]{1, 2, 3});
        UserViewDTO userViewDTO = new UserViewDTO();
        userViewDTO.setProfilePicture(Base64.getEncoder().encodeToString(new byte[]{1, 2, 3}));
        when(modelMapper.map(user, UserViewDTO.class)).thenReturn(userViewDTO);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        CustomUserDetails userDetails = new CustomUserDetails(1L, "testUser", "password", List.of(() -> "ROLE_USER"));
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContextHolder.setContext(new SecurityContextImpl(authentication));

        UserViewDTO profile = userService.getCurrentUserProfile();

        assertEquals(userViewDTO, profile);
        assertEquals(Base64.getEncoder().encodeToString(new byte[]{1, 2, 3}), profile.getProfilePicture());
    }

    @Test
    void uploadProfilePicture_shouldSaveProfilePicture() throws IOException {
        User user = new User();
        user.setId(1L);
        byte[] pictureBytes = {1, 2, 3};
        MultipartFile profilePicture = mock(MultipartFile.class);
        when(profilePicture.getBytes()).thenReturn(pictureBytes);
        CustomUserDetails userDetails = new CustomUserDetails(1L, "testUser", "password", List.of(() -> "ROLE_USER"));
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.uploadProfilePicture(profilePicture);

        assertArrayEquals(pictureBytes, user.getProfilePicture());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUserProfile_shouldUpdateUserDetails() {
        User user = new User();
        user.setId(1L);
        CustomUserDetails userDetails = new CustomUserDetails(1L, "testUser", "password", List.of(() -> "ROLE_USER"));
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.updateUserProfile("John", "Doe", "john.doe@example.com", "john_doe");

        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("john_doe", user.getUsername());
        verify(userRepository, times(1)).save(user);
    }
}
