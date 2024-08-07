package com.onlineshop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.onlineshop.model.entity.User;
import com.onlineshop.repository.UserRepository;
import com.onlineshop.service.impl.UserManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UserManagementServiceTest {

    @InjectMocks
    private UserManagementService userManagementService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setUsername("johndoe");
        mockUser.setEmail("john.doe@example.com");
        mockUser.setPassword("password");
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(mockUser));

        List<User> users = userManagementService.getAllUsers();

        assertEquals(1, users.size());
        assertEquals("John", users.get(0).getFirstName());
    }

    @Test
    void testSaveUser() {
        when(passwordEncoder.encode(mockUser.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(mockUser)).thenReturn(mockUser);

        User savedUser = userManagementService.saveUser(mockUser);

        assertEquals("encodedPassword", savedUser.getPassword());
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void testUpdateUser_UserExists() {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setFirstName("Jane");
        updatedUser.setLastName("Doe");
        updatedUser.setUsername("janedoe");
        updatedUser.setEmail("jane.doe@example.com");
        updatedUser.setPassword("newPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(mockUser)).thenReturn(mockUser);

        User result = userManagementService.updateUser(1L, updatedUser);

        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertEquals("encodedNewPassword", result.getPassword());
    }

    @Test
    void testUpdateUser_UserNotFound() {
        User updatedUser = new User();
        updatedUser.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        User result = userManagementService.updateUser(1L, updatedUser);

        assertNull(result);
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userRepository).deleteById(1L);

        userManagementService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

}
