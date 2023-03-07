package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class JdbcAccountDao implements AccountDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

// method for viewing the balance of a user in the database
    @Override
    public BigDecimal viewBalanceByUserId(int userId) {
        String sql = "SELECT balance FROM account WHERE user_id = ?;";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, userId);
    }

// method for getting the account by the user ID in the database
    @Override
    public Account getAccountByUserId(int userId){
        String sql = "SELECT * from account WHERE user_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        Account account = null;
        while (results.next()){
            account = mapRowToAccount(results);
        }
        return account;
    }


    @Override
    public int getUserIdByAccountId(int accountId) {
        String sql = "SELECT user_id FROM account WHERE account_id = ?;";
        return jdbcTemplate.queryForObject(sql, Integer.class,accountId);
    }


    @Override
    public String getUsernameByAccountId(int accountId) {
        String sql = "SELECT username FROM tenmo_user JOIN account USING (user_id) WHERE account_id = ?;";
        return jdbcTemplate.queryForObject(sql, String.class, accountId);
    }


    @Override
    public void update(Account accountToUpdate) {
        String sql = "UPDATE account SET balance = ? WHERE account_id = ?;";
        jdbcTemplate.update(sql, accountToUpdate.getBalance(), accountToUpdate.getAccountId());
    }

    private Account mapRowToAccount(SqlRowSet res){
        Account account = new Account();
        account.setUserId(res.getInt("user_id"));
        account.setAccountId(res.getInt("account_id"));
        account.setBalance(res.getBigDecimal("balance"));
        return account;
    }


}