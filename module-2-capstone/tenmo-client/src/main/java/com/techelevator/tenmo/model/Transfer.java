package com.techelevator.tenmo.model;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class Transfer {

    private int transferId;
    private int transferType;
    private int transferStatus;
    private Account accountFromObject;
    private Account accountToObject;
    private BigDecimal amount;
    private int accountFrom;
    private int accountTo;

    private NumberFormat currency = NumberFormat.getCurrencyInstance();


    public Transfer() {
    }

    public Transfer(int transferType, int transferStatus, int accountFrom, int accountTo, BigDecimal amount) {
        this.transferType = transferType;
        this.transferStatus = transferStatus;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public int getTransferType() {
        return transferType;
    }

    public void setTransferType(int transferType) {
        this.transferType = transferType;
    }

    public int getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(int transferStatus) {
        this.transferStatus = transferStatus;
    }

    public Account getAccountFromObject() {
        return accountFromObject;
    }

    public void setAccountFromObject(Account accountFromObject) {
        this.accountFromObject = accountFromObject;
    }

    public Account getAccountToObject() {
        return accountToObject;
    }

    public void setAccountToObject(Account accountToObject) {
        this.accountToObject = accountToObject;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(int accountFrom) {
        this.accountFrom = accountFrom;
    }

    public int getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(int accountTo) {
        this.accountTo = accountTo;
    }

    public String displayTransferType(Integer transferType) {
        if (transferType == 1) {
            return "Request";
        } else if (transferType == 2) {
            return "Send";
        }
        return transferType.toString();
    }

    public String displayTransferStatus(Integer transferStatus) {
        if (transferStatus == 1) {
            return "Pending";
        } else if (transferStatus == 2) {
            return "Approved";
        } else if (transferStatus == 3) {
            return "Rejected";
        } else {
            return transferStatus.toString();
        }
    }


    @Override
    public String toString() {

        return "\n****************************************************************" +
                "\n Transfer Details: " +
                "\n****************************************************************" +
                "\n Transfer ID:    " + transferId +
                "\n From Account:   " + accountFrom +
                "\n To Account:     " + accountTo +
                "\n Type:           " + displayTransferType(transferType) +
                "\n Status:         " + displayTransferStatus(transferStatus) +
                "\n Amount:         " + currency.format(amount) +
                "\n****************************************************************";
    }
}