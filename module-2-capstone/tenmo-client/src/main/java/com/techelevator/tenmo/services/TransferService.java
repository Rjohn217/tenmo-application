package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class TransferService {
    private static final String API_BASE_URL = "http://localhost:8080/tenmo/";

    private RestTemplate restTemplate = new RestTemplate();
    private String authToken;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

// method to allow user to view all transfers
    public Transfer[] listAllTransfersCurrentUser(int id) {
        Transfer[] currentUserTransfers = null;
        try {
            ResponseEntity<Transfer[]> response = restTemplate.exchange(API_BASE_URL + "transfers/" + id,
                    HttpMethod.GET, makeAuthEntity(), Transfer[].class);
            currentUserTransfers = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return currentUserTransfers;
    }

// method to allow the user to create a transfer
    public Transfer createTransfer(Transfer transfer){
        HttpEntity<Transfer> entity = makeTransferEntity(transfer);
        Transfer newTransfer = null;
        try{
            ResponseEntity<Transfer> response = restTemplate.exchange(API_BASE_URL + "transfers", HttpMethod.POST, entity, Transfer.class);
            newTransfer = response.getBody();
        }catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return newTransfer;
    }

    public boolean updateTransferStatusId( Transfer transferStatusId) {
        HttpEntity<Transfer> entity = makeTransferEntity(transferStatusId);
        boolean success = false;
        try{
            restTemplate.put(API_BASE_URL + "transfer/" + transferStatusId.getTransferStatus(), entity);
            success = true;
        }catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return  success;
    }

// allows user to retrieve pending transfers
    public Transfer[] getPendingTransfers(int id){
        Transfer[] pendingTransfers = null;
        try{
            ResponseEntity<Transfer[]> response = restTemplate.exchange(API_BASE_URL + "transfers/pending/"+ id, HttpMethod.GET, makeAuthEntity(), Transfer[].class);
            pendingTransfers = response.getBody();
        }catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return pendingTransfers;
    }


    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(transfer, headers );
    }
    private HttpEntity<Void> makeAuthEntity(){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }
}