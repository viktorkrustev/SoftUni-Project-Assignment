package com.onlineshop;

import com.onlineshop.config.Config;
import com.onlineshop.model.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class UserRestClient {
    private final RestTemplate restTemplate;
    private final Config appConfig;

    @Autowired
    public UserRestClient(RestTemplate restTemplate, Config appConfig) {
        this.restTemplate = restTemplate;
        this.appConfig = appConfig;
    }

    public List<UserDTO> getAllUsers() {
        String url = appConfig.getUrl() + "/users";
        ResponseEntity<List<UserDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<UserDTO>>() {}
        );
        return response.getBody();
    }

    public UserDTO addUser(UserDTO userDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);

        try {
            ResponseEntity<UserDTO> response = restTemplate.postForEntity(
                    appConfig.getUrl() + "/users",
                    request,
                    UserDTO.class
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw e;
        }
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        String url = appConfig.getUrl() + "/users/" + id;
        restTemplate.put(url, userDTO);
        return userDTO;
    }

    public void deleteUser(Long id) {
        String url = appConfig.getUrl() + "/users/" + id;
        restTemplate.delete(url);
    }
}

