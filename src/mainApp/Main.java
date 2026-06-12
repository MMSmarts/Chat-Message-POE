package mainApp;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private static String loggedInUser = "";

    public static void main(String[] args) {
        Scanner inputScanner = new Scanner(System.in);

        System.out.println("===== REGISTRATION =====");
        System.out.print("Enter first name: ");
        String userFirstName = inputScanner.nextLine();
        System.out.print("Enter last name: ");
        String userLastName = inputScanner.nextLine();
        System.out.print("Enter username: ");
        String userUsername = inputScanner.nextLine();
        System.out.print("Enter password: ");
        String userPassword = inputScanner.nextLine();
        System.out.print("Enter South African cell phone number (include +27): ");
        String userPhoneNumber = inputScanner.nextLine();

        Login accountManager = new Login(userFirstName, userLastName, userUsername, userPassword, userPhoneNumber);
        System.out.println(accountManager.registerUser());

        if (accountManager.checkUserName() && accountManager.checkPasswordComplexity() && accountManager.checkCellPhoneNumber()) {
            System.out.println("\n===== LOGIN =====");
            System.out.print("Enter username: ");
            String loginUsername = inputScanner.nextLine();
            System.out.print("Enter password: ");
            String loginPassword = inputScanner.nextLine();
            System.out.println(accountManager.returnLoginStatus(loginUsername, loginPassword));

            if (accountManager.loginUser(loginUsername, loginPassword)) {
                loggedInUser = userFirstName + " " + userLastName;
                System.out.println("\nWelcome to QuickChat.");
                
                // Load existing stored messages from JSON file into arrays
                Message.loadStoredMessagesIntoArray();
                
                int menuOption = 0;
                while (menuOption != 5) {
                    System.out.println("\n===== QUICKCHAT MENU =====");
                    System.out.println("1) Send Messages");
                    System.out.println("2) Show recently sent messages");
                    System.out.println("3) Stored Messages");
                    System.out.println("4) Message Reports");
                    System.out.println("5) Quit");
                    System.out.println("6) Populate Test Data (For Testing)");
                    System.out.print("Choose an option: ");
                    
                    menuOption = inputScanner.nextInt();
                    inputScanner.nextLine();
                    
                    switch (menuOption) {
                        case 1:
                            sendMessages(inputScanner);
                            break;
                        case 2:
                            showRecentMessages();
                            break;
                        case 3:
                            storedMessagesMenu(inputScanner);
                            break;
                        case 4:
                            messageReportsMenu(inputScanner);
                            break;
                        case 5:
                            System.out.println("Exiting QuickChat.");
                            break;
                        case 6:
                            Message.populateTestData();
                            break;
                        default:
                            System.out.println("Invalid option selected.");
                    }
                }
            }
        }
        inputScanner.close();
    }
    
    public static void sendMessages(Scanner inputScanner) {
        System.out.print("How many messages would you like to send? ");
        int numberOfMessages = inputScanner.nextInt();
        inputScanner.nextLine();
        
        for (int count = 1; count <= numberOfMessages; count++) {
            System.out.println("\n===== MESSAGE " + count + " =====");
            String messageID = generateMessageID();
            System.out.print("Enter recipient number (+27XXXXXXXXX): ");
            String recipient = inputScanner.nextLine();
            System.out.print("Enter message: ");
            String messageText = inputScanner.nextLine();
            
            Message message = new Message(messageID, count, recipient, loggedInUser, messageText);
            
            // Validate Message ID
            if (!message.checkMessageID()) {
                System.out.println("Message ID is invalid.");
                continue;
            }
            
            // Validate Recipient
            if (message.checkRecipientCell() == 0) {
                System.out.println(message.checkRecipientCellString());
                continue;
            } else {
                System.out.println("Cell phone number successfully captured.");
            }
            
            // Validate Message Length
            String lengthCheck = message.checkMessageLengthResult();
            System.out.println(lengthCheck);
            if (!message.checkMessageLength()) {
                continue;
            }
            
            // Get user choice for send/store/disregard
            System.out.println("\nChoose an option:");
            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message");
            System.out.print("Enter option: ");
            int sendOption = inputScanner.nextInt();
            inputScanner.nextLine();
            
            System.out.println(message.sentMessage(sendOption));
            System.out.println("\n===== MESSAGE DETAILS =====");
            System.out.println(message.printMessage());
        }
        
        System.out.println("\nTotal messages sent: " + Message.returnTotalMessages());
        System.out.println("Total messages stored: " + Message.returnTotalStored());
        System.out.println("Total messages disregarded: " + Message.returnTotalDisregarded());
    }
    
    public static void showRecentMessages() {
        // POE Requirement: This feature is still in development
        System.out.println("\n===== RECENTLY SENT MESSAGES =====");
        System.out.println("Coming Soon.");
    }
    
    public static void storedMessagesMenu(Scanner inputScanner) {
        int option = 0;
        while (option != 7) {
            System.out.println("\n===== STORED MESSAGES MENU =====");
            System.out.println("1) Display sender and recipient of all stored messages");
            System.out.println("2) Display longest stored message");
            System.out.println("3) Search for a message by ID");
            System.out.println("4) Search for all messages for a recipient");
            System.out.println("5) Delete a message using message hash");
            System.out.println("6) Display full report");
            System.out.println("7) Back to Main Menu");
            System.out.print("Choose an option: ");
            option = inputScanner.nextInt();
            inputScanner.nextLine();
            
            switch (option) {
                case 1:
                    Message.displayStoredMessagesSenderAndRecipient();
                    break;
                case 2:
                    System.out.println("\nLongest stored message: " + Message.getLongestStoredMessage());
                    break;
                case 3:
                    System.out.print("Enter Message ID to search: ");
                    String searchID = inputScanner.nextLine();
                    System.out.println(Message.searchByMessageID(searchID));
                    break;
                case 4:
                    System.out.print("Enter recipient number (+27XXXXXXXXX): ");
                    String searchRecipient = inputScanner.nextLine();
                    ArrayList<String> results = Message.searchByRecipient(searchRecipient);
                    System.out.println("\nMessages found for " + searchRecipient + ":");
                    if (results.isEmpty()) {
                        System.out.println("No messages found.");
                    } else {
                        for (String msg : results) {
                            System.out.println("- " + msg);
                        }
                    }
                    break;
                case 5:
                    System.out.print("Enter Message Hash to delete: ");
                    String hashToDelete = inputScanner.nextLine();
                    System.out.println(Message.deleteByMessageHash(hashToDelete));
                    break;
                case 6:
                    Message.displayFullReport();
                    break;
                case 7:
                    System.out.println("Returning to main menu...");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
    
    public static void messageReportsMenu(Scanner inputScanner) {
        int option = 0;
        while (option != 2) {
            System.out.println("\n===== MESSAGE REPORTS MENU =====");
            System.out.println("1) Display full report of all stored messages");
            System.out.println("2) Back to Main Menu");
            System.out.print("Choose an option: ");
            option = inputScanner.nextInt();
            inputScanner.nextLine();
            
            switch (option) {
                case 1:
                    Message.displayFullReport();
                    break;
                case 2:
                    System.out.println("Returning to main menu...");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    public static String generateMessageID() {
        Random random = new Random();
        long number = 1000000000L + (long)(random.nextDouble() * 9000000000L);
        return String.valueOf(number);
    }
}