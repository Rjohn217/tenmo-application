package com.techelevator.tenmo.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

public class Account {

    @Positive
    private int AccountId;
    @Positive
    private int userId;
    @NotNull
    private BigDecimal balance;

    public Account() {
    }

// setters for the users account     
    public Account(int accountId, int userId, BigDecimal balance) {
        AccountId = accountId;
        this.userId = userId;
        this.balance = balance;
    }

    public int getAccountId() {
        return AccountId;
    }

    public void setAccountId(int accountId) {
        AccountId = accountId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
