package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/tenmo/accounts")
public class AccountController {

    private AccountDao accountDao;
    private UserDao userDao;

    public AccountController(AccountDao accountDao, UserDao userDao) {
        this.accountDao = accountDao;
        this.userDao = userDao;
    }

    @GetMapping("/{id}/balance")
    public BigDecimal getBalance(@PathVariable int id) {
        try {
            return accountDao.viewBalanceByUserId(id);
        } catch(ResourceAccessException | RestClientResponseException e) {
            throw new RuntimeException("Error getting account balance for user: " + id, e);
        }
    }


    @GetMapping("/user/{id}")
    public Account getAccountByUserId(@PathVariable int id) {
        try {
            return accountDao.getAccountByUserId(id);
        } catch (ResourceAccessException | RestClientResponseException e) {
            throw new RuntimeException("Error getting account by user ID: " + id, e);
        }
    }


    @GetMapping("{id}/username")
    public String getUsernameByAccountId(@PathVariable int id) {
        try{
            return accountDao.getUsernameByAccountId(id);
        } catch (ResourceAccessException | RestClientResponseException e) {
            throw new RuntimeException("Error getting username by account ID: " + id, e);
        }
    }


    @GetMapping("/{id}")
    public int getUserIdByAccountId(@PathVariable int id) {
        try{
            return accountDao.getUserIdByAccountId(id);
        } catch (ResourceAccessException | RestClientResponseException e) {
            throw new RuntimeException("Error getting user ID by account ID: " + id, e);
        }
    }


    @PutMapping("/{id}")
    public void update(@PathVariable int id, @Valid @RequestBody Account accountToUpdate) {
        //handle errors
        try {
            accountDao.update(accountToUpdate);
        } catch(ResourceAccessException | RestClientResponseException e) {
            throw new RuntimeException("Unable to update given account with id: " + id, e);
        }
    }
}