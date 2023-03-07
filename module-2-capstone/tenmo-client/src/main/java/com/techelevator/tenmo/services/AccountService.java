package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

// URL to be used by the account service, authentication token, also getters and setters
public class AccountService {

    private static final String API_BASE_URL = "http://localhost:8080/tenmo/";

    private RestTemplate restTemplate = new RestTemplate();
    private String authToken;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

// to retrieve the current balance of the current user throws exception is if information is invalid and logs it in the logger class
    public BigDecimal getCurrentUserAccountBalance(int userId) {
        BigDecimal userAccountBalance = null;
        try {
            ResponseEntity<BigDecimal> response =
                    restTemplate.exchange(API_BASE_URL + "accounts/" + userId + "/balance",
                            HttpMethod.GET, makeAuthEntity(), BigDecimal.class);
            userAccountBalance = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return userAccountBalance;
    }

// TODO allows user to retrieve account by user ID       (clarification on this one)-liz
    public Account getAccountByUserId(int userId){
        Account account = null;
        try{
            ResponseEntity<Account> response = restTemplate.exchange(
                    API_BASE_URL + "accounts/user/" + userId,
                        HttpMethod.GET, makeAuthEntity(), Account.class);
            account = response.getBody();
        }
        catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return account;
    }

//TODO to retrieve the user ID by their account ID    (clarification on this one)
    public int getUserIdByAccountId(int accountId) {
        int userId = 0;
        try {
            ResponseEntity<Integer> response =
                    restTemplate.exchange(API_BASE_URL + "accounts/" + accountId,
                            HttpMethod.GET, makeAuthEntity(), Integer.class);
            userId = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            System.out.println(e.getMessage());
        }
        return userId;
    }

// to retrieve username by thier account ID
    public String getUsernameByAccountId(int accountId) {
        String username = null;
        try {
            ResponseEntity<String> response =
                    restTemplate.exchange(API_BASE_URL + "accounts/" + accountId + "/username",
                            HttpMethod.GET, makeAuthEntity(), String.class);
            username = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return username;
    }

    public boolean update(Account account) {
        HttpEntity<Account> entity = makeAccountEntity(account);

        boolean success = false;
        try {
            restTemplate.put(API_BASE_URL + "accounts/" + account.getAccountId(), entity);
            success = true;
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return success;
    }

    public void receiveTeBucks(int receivedId, int senderId, BigDecimal amount){
        try{
            ResponseEntity<Void> response = restTemplate.exchange(API_BASE_URL + "receive/" + receivedId + "/" + senderId + "/" + amount, HttpMethod.POST, makeAuthEntity(), Void.class);
        }catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }

    public HttpEntity<Account> makeAccountEntity(Account account) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(account, headers);
    }
}
