package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/tenmo/transfers")
public class TransferController {

    private AccountDao accountDao;

    private TransferDao transferDao;

    private UserDao userDao;

    public TransferController(AccountDao accountDao, TransferDao transferDao, UserDao userDao) {
        this.accountDao = accountDao;
        this.transferDao = transferDao;
        this.userDao = userDao;
    }

    @GetMapping("/{id}")
    public List<Transfer> getAllTransfersByAccountId(@PathVariable int id) {
        try{
            return transferDao.getAllTransfersByAccountId(id);
        } catch (ResourceAccessException | RestClientResponseException e) {
            throw new RuntimeException("Unable to retrieve transfer history for account id: " + id, e);
        }
    }

// create the transfer method in the database

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Transfer makeTransfer(@Valid @RequestBody Transfer transfer) {
        BigDecimal zero = new BigDecimal("0");
        BigDecimal amount = transfer.getAmount();

        try {
            if (transfer.getAccountFrom() != transfer.getAccountTo() && amount.compareTo(zero) > 0) {
                return transferDao.createTransfer(transfer);
            }
            return new Transfer();
        } catch (ResourceAccessException | RestClientResponseException e) {
            throw new RuntimeException("Unable to create transfer", e);
        }
        //need to add output in case balance is too low
    }

    @PutMapping("/transferStatus/{id}")
    public void updateTransferStatusId(@PathVariable int id, @Valid @RequestBody Transfer transferStatus) {
        //handle errors
        transferDao.updateTransferStatusId(transferStatus);

    }

}
