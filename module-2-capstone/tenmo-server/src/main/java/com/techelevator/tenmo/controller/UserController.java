package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// verify authentication in the database also set role for authorization
@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/tenmo/users")
public class UserController {

    private UserDao userDao;

    public UserController(UserDao userDao) {
        this.userDao = userDao;
    }
    @PreAuthorize("permitAll")
    @GetMapping
    public List<User> getAllUsers() {
        return userDao.findAll();
    }
}

