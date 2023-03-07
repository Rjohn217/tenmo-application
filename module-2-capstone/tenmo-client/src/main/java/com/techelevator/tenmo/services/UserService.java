package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class UserService {

    private static final String API_BASE_URL = "http://localhost:8080/tenmo/";

    private RestTemplate restTemplate = new RestTemplate();

    private AccountService accountService = new AccountService();

    private TransferService transferService = new TransferService();

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    private String authToken;

// method to retrieve users onto the console
    public User[] getAllUsers() {
        User[] users = null;
        try {
            ResponseEntity<User[]> entity = restTemplate.exchange(API_BASE_URL + "users", HttpMethod.GET, makeAuthEntity(), User[].class);
            users = entity.getBody();
        } catch(RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return users;
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }
}

