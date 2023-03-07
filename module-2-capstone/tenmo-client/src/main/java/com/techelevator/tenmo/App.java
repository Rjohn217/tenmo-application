package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class
App {

    //TODO: Add try catch to any time talking to server or user, anything dangerous
    //TODO: Service layer, controller layer
    //TODO: Custom exceptions if user doesn't exist, etc.

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    private AuthenticatedUser currentUser;
    private TransferService transferService = new TransferService();

    private AccountService accountService = new AccountService();
    private UserService userService = new UserService();

    private NumberFormat currency = NumberFormat.getCurrencyInstance();

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }

    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            System.out.println("Invalid username or password. Please try again");
        }
        accountService.setAuthToken(currentUser.getToken());
        transferService.setAuthToken(currentUser.getToken());
        userService.setAuthToken(currentUser.getToken());
    }

    private void handleLogout() {
        currentUser = null;
        run();
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance(currentUser.getUser().getId());
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 6) {
                handleLogout();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

// client is able to vew current balance in tenmo app
    private void viewCurrentBalance(int id) {
        BigDecimal currentBalance = accountService.getCurrentUserAccountBalance(id);
        System.out.println("");
        System.out.println("****************************************************************");
        System.out.println("Your current balance is : " + currency.format(currentBalance));
        System.out.println("****************************************************************");

    }
// to view the transfer history between both sender and receiver ( ID of the person sending the money and the person receiving it also the amount)
    private void viewTransferHistory() {
        int accountId = accountService.getAccountByUserId(currentUser.getUser().getId()).getAccountId();
        Transfer[] currentUserTransfers = transferService.listAllTransfersCurrentUser(accountId);
        System.out.println("");
        System.out.println("****************************************************************");

        System.out.println("TRANSFERS");
        System.out.println("ID\t\tFROM\t\tTO\t\t\tAMOUNT");
        System.out.println("****************************************************************");

        for (Transfer transfer : currentUserTransfers) {
            int transferId = transfer.getTransferId();
            String usernameFrom = accountService.getUsernameByAccountId(transfer.getAccountFrom());
            String usernameTo = accountService.getUsernameByAccountId(transfer.getAccountTo());
            BigDecimal amount = transfer.getAmount();
            System.out.println(String.format("%-7s %-11s %-11s %s" , transferId, usernameFrom, usernameTo, amount));
        }
        System.out.println("****************************************************************");



        System.out.println("");
        int promptedTransferId = consoleService.promptForInt("Enter transfer ID to see more details or 0 to return: ");
        if (promptedTransferId == 0) {
            return;
        }

        for (Transfer transfer : currentUserTransfers) {
            if (transfer.getTransferId() == promptedTransferId) {
                System.out.println(transfer);
            }
        }
    }

// user will be able to view outgoing/ incoming transfers (using user ID)
    private void viewPendingRequests() {
        // TODO Auto-generated method stub
//        consoleService.printPendingMessage();
        int accountId = accountService.getAccountByUserId(currentUser.getUser().getId()).getAccountId();
        Transfer[] currentUserTransfers = transferService.listAllTransfersCurrentUser(accountId);
        for (Transfer transfer : currentUserTransfers) {
            int transferId = transfer.getTransferId();
            int transferTypeId = transfer.getTransferType();
            int transferStatusId = transfer.getTransferStatus();
            String usernameFrom = currentUser.getUser().getUsername();
            String usernameTo = accountService.getUsernameByAccountId(transfer.getAccountTo());
            BigDecimal amount = transfer.getAmount();
            if(transferTypeId == 1){
                if(accountId != transfer.getAccountFrom()){
                    System.out.println(transferId  + "\t" + accountService.getUsernameByAccountId(transfer.getAccountFrom()) + "\t\t" + amount);
                }

            }


        }

        int transferIdAction = consoleService.promptForInt("Please enter transfer ID to approve/reject (0 to cancel): ");
        Account receiverAccount = accountService.getAccountByUserId(currentUser.getUser().getId());

        int menuSelection = -1;
        consoleService.printHandleRequestMenu();
        menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
        if (menuSelection == 1) {
            for(Transfer trans: currentUserTransfers){
                Account senderAccount  = accountService.getAccountByUserId(accountService.getUserIdByAccountId(trans.getAccountFrom()));

                int transferId = trans.getTransferId();
                BigDecimal amount = trans.getAmount();
                int transferTypeId = trans.getTransferType();
                if(transferTypeId == 1 && transferId == transferIdAction ){
                    BigDecimal receiverBal = receiverAccount.getBalance().subtract(amount);
                    receiverAccount.setBalance(receiverBal);
                    BigDecimal senderBal = senderAccount.getBalance().add(amount);
                    senderAccount.setBalance(senderBal);
                    trans.setTransferStatus(2);
                    transferService.updateTransferStatusId(trans);

                    accountService.update(senderAccount);
                    accountService.update(receiverAccount) ;
                    System.out.println("");
                    System.out.println("Transfer Approved!");

                }
            }

        } else if (menuSelection == 2) {
            System.out.println("Transfer Rejected");
            return;
        } else if (menuSelection != 0) {
            System.out.println("Invalid Selection");
            consoleService.pause();
            return;
        }


    }

// method to send tenmo buck to other users if account has sufficient funds and if ID is valid also is sender has a valid account
    private void sendBucks() {
        User[] listOfUser = userService.getAllUsers();
        System.out.println("");
        System.out.println("****************************************************************");
        System.out.println("Choose a friend to send Bucks to!");
        System.out.println("****************************************************************");

        System.out.println("USER ID\t\tUSERNAME");

        for (User user : listOfUser) {
            if (!user.getUsername().equals(currentUser.getUser().getUsername()))
                System.out.println(user.getId() + "\t\t" + user.getUsername());
        }

        System.out.println("****************************************************************");

        System.out.println("");

        List<Integer> userIds = new ArrayList<>();
        for (var user : listOfUser) {
            userIds.add(user.getId());
        }

        int receiverId = 0;
        outer:
        while (true) {
            receiverId = consoleService.promptForInt("Please enter their ID to continue or 0 to return: ");
            for (var id: userIds) {
                if (id.equals(receiverId)) {
                    break outer;
                }
            }

            if (receiverId == 0) {
                break;
            }
            if (currentUser.getUser().getId() == receiverId) {
                System.out.println();
                System.out.println("Sorry, you can't send money to yourself, but you can send money to a friend instead!");
            }
            System.out.println("");
            System.out.println("Please enter a valid ID");
            System.out.println("");
        }
        if (receiverId == 0) {
            return;
        }

        //fetch the account with id
        Account receiverAccount = accountService.getAccountByUserId(receiverId);
        Account senderAccount = accountService.getAccountByUserId(currentUser.getUser().getId());

        System.out.println("");
        BigDecimal amount = consoleService.promptForBigDecimal("How much would you like to send to " +
                accountService.getUsernameByAccountId(receiverAccount.getAccountId()) + "?: ");

        System.out.println("");    // second form of confirmation to make sur user is sending bucks to the correct person
        System.out.println("Are you sure you would like to send " +currency.format(amount) + " to " + accountService.getUsernameByAccountId(receiverAccount.getAccountId()) + "?");
        String userConfirmation = consoleService.promptForString("Y/N: ");
        if(userConfirmation.equalsIgnoreCase("y")) {

            BigDecimal zero = new BigDecimal("0");

            //check if amount sending is greater than zero and not greater than balance
            if (amount.compareTo(zero) > 0 && senderAccount.getBalance().compareTo(amount) > 0) {
                //check if account null
                if (receiverAccount != null && senderAccount != null) {
                    BigDecimal newBalance = receiverAccount.getBalance().add(amount);
                    receiverAccount.setBalance(newBalance);
                    newBalance = senderAccount.getBalance().subtract(amount);
                    senderAccount.setBalance(newBalance);

                    //call on transfer and update
                    //also need to implement logic to ensure that account updates happen unless transfer successful too
                    Transfer newTransfer = new Transfer(2, 2, senderAccount.getAccountId(), receiverAccount.getAccountId(), amount);
                    transferService.createTransfer(newTransfer);

                    if (accountService.update(senderAccount) && accountService.update(receiverAccount)) {
                        System.out.println("");
                        System.out.println("Your transfer was Successful!");
                        System.out.println("");
                        System.out.println("****************************************************************");
                        System.out.println("Your new balance is: " + currency.format(senderAccount.getBalance()));
                        System.out.println("****************************************************************");

                    } else {
                        System.out.println("Transfer Failed");
                    }
                }
            } else {
                System.out.println("");
                System.out.println("The amount you are sending must be less than your account balance " +
                        "and greater than zero. Please try again.");
                return;
            }
        } else {
            return;
        }
    }

// method for the user to request buck from other tenmo users if all parameters are met (valid user account, ID, balance)
    private void requestBucks () {
//        consoleService.printRequestMessage();
        // TODO Auto-generated method stub
        User[] allUsers = userService.getAllUsers();
        boolean isValidId = false;


        for(User user: allUsers){
            if (!user.getUsername().equals(currentUser.getUser().getUsername()))
            System.out.println(user.getId() + "   " + user.getUsername());
        }
        System.out.println("----------------------");
        int receiver = 0;
        do{
            receiver = consoleService.promptForInt("Please enter the ID of user you're requesting from : ");
            if(receiver == 0 || accountService.getAccountByUserId(receiver) == null){
                System.out.println("(*_*) Sorry invalid user ID, Please try again: ");
                isValidId = false;
            }
            else if(currentUser.getUser().getId() == receiver){
                System.out.println();
                System.out.println("Sorry, you can't request money from yourself: ");
                isValidId = false;
            }
            else{
                isValidId = true;
            }
        }while(!isValidId);

        Account senderAccount = accountService.getAccountByUserId(currentUser.getUser().getId());
        BigDecimal zero = BigDecimal.ZERO;
        BigDecimal amount = consoleService.promptForBigDecimal("Please enter the amount: ");
        while(amount.compareTo(zero)<=0 || amount.compareTo(senderAccount.getBalance()) >0){
            amount = consoleService.promptForBigDecimal("Amount must be greater than zero try again and less or equal to your balance: ");
        }


        Account receiverAccount = accountService.getAccountByUserId(receiver);


        if(senderAccount == null || receiverAccount == null){
            System.out.println("Here");
            return;
        }

        Transfer requestTransfer = new Transfer(1, 1, senderAccount.getAccountId(), receiverAccount.getAccountId(), amount);
        transferService.createTransfer(requestTransfer);
        System.out.println("Request Completed");



    }
    }




