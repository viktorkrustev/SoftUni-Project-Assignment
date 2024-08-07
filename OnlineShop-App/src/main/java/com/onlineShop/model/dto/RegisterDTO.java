package com.onlineshop.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.UniqueElements;

public class RegisterDTO {

    @Size(min = 3, max = 20, message = "First name must be between 3 and 20 characters!")
    private String firstName;
    @Size(min = 3, max = 20, message = "Last name must be between 3 and 20 characters!")
    private String lastName;

    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters!")
    private String username;

    @NotBlank(message = "Email cannot be empty!")
    @Email(message = "Invalid email format!")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters!")
    private String password;

    @NotBlank(message = "Confirm password is required!")
    private String confirmPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
