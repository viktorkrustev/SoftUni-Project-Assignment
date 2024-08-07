package com.onlineshop.controller;

import com.onlineshop.config.SecurityConfig;
import com.onlineshop.model.dto.RegisterDTO;
import com.onlineshop.model.dto.UserViewDTO;
import com.onlineshop.service.OrderService;
import com.onlineshop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        when(userService.getCurrentUserProfile()).thenReturn(new UserViewDTO());
        when(orderService.getOrdersForCurrentUser()).thenReturn(Collections.emptyList());
    }

    @Test
    @WithMockUser
    public void testShowRegisterForm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    public void testRegisterUserSuccess() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("newUser");
        registerDTO.setPassword("password");
        registerDTO.setConfirmPassword("password");
        registerDTO.setEmail("user@example.com");

        mockMvc.perform(post("/users/register")
                        .flashAttr("registerDTO", registerDTO)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/login"));
    }



    @Test
    public void testRegisterUserPasswordMismatch() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("newUser");
        registerDTO.setPassword("password");
        registerDTO.setConfirmPassword("differentPassword");
        registerDTO.setEmail("user@example.com");

        mockMvc.perform(post("/users/register")
                        .flashAttr("registerDTO", registerDTO)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("passwordError"))
                .andExpect(model().attribute("passwordError", "Passwords do not match"));
    }

    @Test
    @WithMockUser
    public void testShowLoginForm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @WithMockUser
    public void testGetProfile() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("orders"));
    }

    @Test
    @WithMockUser
    public void testUpdateProfile() throws Exception {
        mockMvc.perform(post("/users/updateProfile")
                        .with(csrf())
                        .param("first_name", "John")
                        .param("last_name", "Doe")
                        .param("email", "john.doe@example.com")
                        .param("username", "john_doe"))
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/users/profile"));

        verify(userService, times(1)).updateUserProfile("John", "Doe", "john.doe@example.com", "john_doe");
    }

    @Test
    public void testUploadProfilePicture() throws Exception {
        MockMultipartFile profilePicture = new MockMultipartFile("profilePicture", "test.jpg", "image/jpeg", "test image".getBytes());

        mockMvc.perform(multipart("/users/uploadProfilePicture")
                        .file(profilePicture)
                        .with(csrf())
                        .with(user("testUser").roles("USER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/profile"));

        verify(userService, times(1)).uploadProfilePicture(any(MultipartFile.class));
    }

}
