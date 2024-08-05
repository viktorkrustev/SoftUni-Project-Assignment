package com.onlineshop.util;

import com.onlineshop.util.CustomUserDetails;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomUserDetailsTest {

    @Test
    public void testConstructorAndGettersWithMock() {
        // Arrange
        Long expectedId = 1L;
        String expectedUsername = "testuser";
        String expectedPassword = "password";

        // Мокване на GrantedAuthority
        GrantedAuthority mockAuthority = mock(GrantedAuthority.class);
        when(mockAuthority.getAuthority()).thenReturn("ROLE_USER");

        Collection<GrantedAuthority> authorities = Collections.singletonList(mockAuthority);

        // Act
        CustomUserDetails userDetails = new CustomUserDetails(
                expectedId,
                expectedUsername,
                expectedPassword,
                authorities
        );

        // Assert
        assertEquals(expectedId, userDetails.getId());
        assertEquals(expectedUsername, userDetails.getUsername());
        assertEquals(expectedPassword, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().contains(mockAuthority));
    }

    @Test
    public void testSettersWithMock() {
        // Arrange
        CustomUserDetails userDetails = new CustomUserDetails();
        Long expectedId = 2L;
        String expectedUsername = "anotheruser";
        String expectedPassword = "newpassword";

        // Мокване на GrantedAuthority
        GrantedAuthority mockAuthority = mock(GrantedAuthority.class);
        when(mockAuthority.getAuthority()).thenReturn("ROLE_ADMIN");

        Collection<GrantedAuthority> authorities = Collections.singletonList(mockAuthority);

        // Act
        userDetails.setId(expectedId);
        userDetails.setUsername(expectedUsername);
        userDetails.setPassword(expectedPassword);
        userDetails.setAuthorities(authorities);

        // Assert
        assertEquals(expectedId, userDetails.getId());
        assertEquals(expectedUsername, userDetails.getUsername());
        assertEquals(expectedPassword, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().contains(mockAuthority));
    }

    @Test
    public void testUserDetailsMethods() {
        // Arrange
        CustomUserDetails userDetails = new CustomUserDetails(
                1L,
                "testuser",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        // Act & Assert
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }
}
