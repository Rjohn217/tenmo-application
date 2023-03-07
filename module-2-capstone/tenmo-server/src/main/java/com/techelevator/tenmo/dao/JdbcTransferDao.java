package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

// to get all transfers by account and returning the information to the database
    @Override
    public List<Transfer> getAllTransfersByAccountId(int id) {
        List<Transfer> allTransfersByAccountId = new ArrayList<>();
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount FROM transfer " +
                "WHERE account_from = ? OR account_to = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id, id);
        while (results.next()) {
            allTransfersByAccountId.add(mapRowToTransfer(results));
        }
        return allTransfersByAccountId;
    }

    // to create a transfer and returning the information to the database
    @Override
    public Transfer createTransfer(Transfer transfer){
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (?,?,?,?,?);";
        jdbcTemplate.update(sql, transfer.getTransferType(), transfer.getTransferStatus(), transfer.getAccountFrom(), transfer.getAccountTo(),transfer.getAmount());
        return transfer;
    }

    public void updateTransferStatusId( Transfer transferStatusId) {
        String sql = "UPDATE transfer SET transfer_status_id = 2 WHERE transfer_id = ?;";
        jdbcTemplate.update(sql, transferStatusId.getTransferStatus(), transferStatusId.getTransferId());
    }



    private Transfer mapRowToTransfer(SqlRowSet res){
        Transfer transfer = new Transfer();
        transfer.setTransferId(res.getInt("transfer_id"));
        transfer.setTransferType(res.getInt("transfer_type_id"));
        transfer.setTransferStatus(res.getInt("transfer_status_id"));
        transfer.setAccountFrom(res.getInt("account_from"));
        transfer.setAccountTo(res.getInt("account_to"));
        transfer.setAmount(res.getBigDecimal("amount"));

        return transfer;
    }
}
