package com.onlineShop.controller;

import com.onlineShop.model.dto.UserDTO;
import com.onlineShop.model.entity.User;
import com.onlineShop.service.impl.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
    private final UserManagementService userManagementService;

    @Autowired
    public UserRestController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userManagementService.getAllUsers().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        System.out.println("Received UserDTO: " + userDTO);

        if (userDTO.getFirstName() == null || userDTO.getFirstName().isEmpty() ||
                userDTO.getLastName() == null || userDTO.getLastName().isEmpty() ||
                userDTO.getUsername() == null || userDTO.getUsername().isEmpty() ||
                userDTO.getEmail() == null || userDTO.getEmail().isEmpty() ||
                userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {

            System.err.println("Validation failed for UserDTO: " + userDTO);
            return ResponseEntity.badRequest().body(null);
        }

        UserDTO createdUser = userManagementService.createUser(userDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }






    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        User user = convertToEntity(userDTO);
        User updatedUser = userManagementService.updateUser(id, user);
        if (updatedUser != null) {
            return ResponseEntity.ok(convertToDTO(updatedUser));
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userManagementService.deleteUser(id);
        return ResponseEntity.ok("User with id " + id + " has been deleted successfully.");
    }

    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        return userDTO;
    }

    private User convertToEntity(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        return user;
    }
}
