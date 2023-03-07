package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;

public interface AccountDao {

    BigDecimal viewBalanceByUserId(int id);

    Account getAccountByUserId(int userID);

    void update(Account accountToUpdate);

    String getUsernameByAccountId(int accountId);

    int getUserIdByAccountId(int id);

}
