package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface TransferDao {


    List<Transfer> getAllTransfersByAccountId(int id);

    Transfer createTransfer(Transfer transfer);

    void updateTransferStatusId( Transfer transferStatusId);


}