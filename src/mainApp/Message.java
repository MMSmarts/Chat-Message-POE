package mainApp;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class Message {
    private String messageID;
    private int messageNumber;
    private String recipient;
    private String sender;
    private String messageText;
    private String messageHash;
    private String flag;

    private static int totalMessagesSent = 0;
    private static int totalMessagesStored = 0;
    private static int totalMessagesDisregarded = 0;

    private static ArrayList<Message> sentMessagesArray = new ArrayList<>();
    private static ArrayList<Message> disregardedMessagesArray = new ArrayList<>();
    private static ArrayList<Message> storedMessagesArray = new ArrayList<>();
    private static ArrayList<String> messageHashesArray = new ArrayList<>();
    private static ArrayList<String> messageIDsArray = new ArrayList<>();

    public Message(String messageID, int messageNumber, String recipient, String sender, String messageText) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.sender = sender;
        this.messageText = messageText;
        this.messageHash = createMessageHash();
        this.flag = "";
    }

    public Message(String messageID, int messageNumber, String recipient, String messageText) {
        this(messageID, messageNumber, recipient, "Unknown", messageText);
    }

    public boolean checkMessageID() {
        return messageID != null && messageID.length() == 10;
    }

    public int checkRecipientCell() {
        if (recipient != null && recipient.matches("^\\+27\\d{9}$")) {
            return 1;
        }
        return 0;
    }

    public String checkRecipientCellString() {
        if (checkRecipientCell() == 1) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
    }

    public boolean checkMessageLength() {
        return messageText != null && messageText.length() <= 250;
    }

    public String checkMessageLengthResult() {
        if (checkMessageLength()) {
            return "Message ready to send.";
        } else {
            int exceededBy = messageText.length() - 250;
            return "Message exceeds 250 characters by " + exceededBy + "; please reduce the size.";
        }
    }

    public String createMessageHash() {
        if (messageText == null || messageText.trim().isEmpty()) {
            String prefix = messageID.length() >= 2 ? messageID.substring(0, 2) : messageID;
            return prefix + ":" + messageNumber + ":EMPTY";
        }

        String[] words = messageText.trim().split("\\s+");

        if (words.length == 1) {
            String singleWord = cleanWord(words[0]).toUpperCase();
            String prefix = messageID.length() >= 2 ? messageID.substring(0, 2) : messageID;
            return prefix + ":" + messageNumber + ":" + singleWord;
        }

        String firstWord = cleanWord(words[0]).toUpperCase();
        String lastWord = cleanWord(words[words.length - 1]).toUpperCase();
        String prefix = messageID.length() >= 2 ? messageID.substring(0, 2) : messageID;

        return prefix + ":" + messageNumber + ":" + firstWord + lastWord;
    }

    private String cleanWord(String word) {
        return word.replaceAll("[^a-zA-Z0-9]$", "");
    }

    public String sentMessage(int option) {
        switch (option) {
            case 1:
                totalMessagesSent++;
                this.flag = "Sent";
                sentMessagesArray.add(this);
                messageHashesArray.add(this.messageHash);
                messageIDsArray.add(this.messageID);
                return "Message successfully sent.";

            case 2:
                totalMessagesDisregarded++;
                this.flag = "Disregarded";
                disregardedMessagesArray.add(this);
                messageHashesArray.add(this.messageHash);
                messageIDsArray.add(this.messageID);
                return "Press 0 to delete message.";

            case 3:
                this.flag = "Stored";
                storeMessage();
                totalMessagesStored++;
                storedMessagesArray.add(this);
                messageHashesArray.add(this.messageHash);
                messageIDsArray.add(this.messageID);
                return "Message successfully stored.";

            default:
                return "Invalid option selected.";
        }
    }

    public String printMessage() {
        return "Message ID: " + messageID +
                "\nMessage Hash: " + messageHash +
                "\nSender: " + sender +
                "\nRecipient: " + recipient +
                "\nMessage: " + messageText;
    }

    public String printStoredMessageReport() {
        return "Message ID: " + messageID +
                "\nMessage Hash: " + messageHash +
                "\nSender: " + sender +
                "\nRecipient: " + recipient +
                "\nMessage: " + messageText +
                "\nStatus: " + flag;
    }

    public void storeMessage() {
        try {
            ArrayList<String> existingMessages = loadStoredMessagesFromJSON();

            String jsonMessage = "{\n";
            jsonMessage += "  \"messageID\": \"" + escapeJson(messageID) + "\",\n";
            jsonMessage += "  \"messageHash\": \"" + escapeJson(messageHash) + "\",\n";
            jsonMessage += "  \"sender\": \"" + escapeJson(sender) + "\",\n";
            jsonMessage += "  \"recipient\": \"" + escapeJson(recipient) + "\",\n";
            jsonMessage += "  \"messageText\": \"" + escapeJson(messageText) + "\",\n";
            jsonMessage += "  \"flag\": \"" + escapeJson(flag) + "\"\n";
            jsonMessage += "}";

            existingMessages.add(jsonMessage);

            FileWriter writer = new FileWriter("storedMessages.json");
            writer.write("[\n");

            for (int i = 0; i < existingMessages.size(); i++) {
                writer.write(existingMessages.get(i));

                if (i < existingMessages.size() - 1) {
                    writer.write(",\n");
                } else {
                    writer.write("\n");
                }
            }

            writer.write("]");
            writer.close();

        } catch (IOException e) {
            System.out.println("Error storing message: " + e.getMessage());
        }
    }

    private String escapeJson(String text) {
        if (text == null) {
            return "";
        }

        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public static ArrayList<String> loadStoredMessagesFromJSON() {
        ArrayList<String> messages = new ArrayList<>();
        File file = new File("storedMessages.json");

        if (!file.exists()) {
            return messages;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            String jsonContent = content.toString();
            int braceCount = 0;
            int start = -1;

            for (int i = 0; i < jsonContent.length(); i++) {
                char c = jsonContent.charAt(i);

                if (c == '{') {
                    if (braceCount == 0) {
                        start = i;
                    }
                    braceCount++;
                } else if (c == '}') {
                    braceCount--;

                    if (braceCount == 0 && start != -1) {
                        String message = jsonContent.substring(start, i + 1);
                        messages.add(message);
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading messages: " + e.getMessage());
        }

        return messages;
    }

    // Load stored messages from JSON into the storedMessagesArray
    public static void loadStoredMessagesIntoArray() {
        ArrayList<String> jsonMessages = loadStoredMessagesFromJSON();
        
        for (String jsonMsg : jsonMessages) {
            StoredMessage sm = parseJSONToStoredMessage(jsonMsg);
            Message msg = new Message(sm.messageID, 0, sm.recipient, sm.sender, sm.messageText);
            msg.messageHash = sm.messageHash;
            msg.flag = sm.flag;
            storedMessagesArray.add(msg);
            messageHashesArray.add(sm.messageHash);
            messageIDsArray.add(sm.messageID);
            
            if (sm.flag.equals("Stored")) {
                totalMessagesStored++;
            } else if (sm.flag.equals("Sent")) {
                totalMessagesSent++;
            } else if (sm.flag.equals("Disregarded")) {
                totalMessagesDisregarded++;
            }
        }
    }

    // Populate test data from POE Page 11
    public static void populateTestData() {
        // Clear existing arrays
        sentMessagesArray.clear();
        disregardedMessagesArray.clear();
        storedMessagesArray.clear();
        messageHashesArray.clear();
        messageIDsArray.clear();
        
        totalMessagesSent = 0;
        totalMessagesStored = 0;
        totalMessagesDisregarded = 0;
        
        // Test Data Message 1 - Sent (POE Page 11)
        Message msg1 = new Message(generateTestMessageID(), 1, "+27834557896", "Developer", "Did you get the cake?");
        msg1.sentMessage(1);
        
        // Test Data Message 2 - Stored (POE Page 11)
        Message msg2 = new Message(generateTestMessageID(), 2, "+27838884567", "Developer", "Where are you? You are late! I have asked you to be on time.");
        msg2.sentMessage(3);
        
        // Test Data Message 3 - Disregarded (POE Page 11)
        Message msg3 = new Message(generateTestMessageID(), 3, "+27834484567", "Developer", "Yohoooo, I am at your gate.");
        msg3.sentMessage(2);
        
        // Test Data Message 4 - Sent (POE Page 11)
        Message msg4 = new Message(generateTestMessageID(), 4, "0838884567", "Developer", "It is dinner time!");
        msg4.sentMessage(1);
        
        // Test Data Message 5 - Stored (POE Page 11)
        Message msg5 = new Message(generateTestMessageID(), 5, "+27838884567", "Developer", "Ok, I am leaving without you.");
        msg5.sentMessage(3);
        
        System.out.println("Test data populated successfully!");
        System.out.println("Sent messages: " + totalMessagesSent);
        System.out.println("Stored messages: " + totalMessagesStored);
        System.out.println("Disregarded messages: " + totalMessagesDisregarded);
    }
    
    private static String generateTestMessageID() {
        return String.valueOf(1000000000L + (long)(Math.random() * 9000000000L));
    }

    public static StoredMessage parseJSONToStoredMessage(String jsonString) {
        StoredMessage msg = new StoredMessage();

        msg.messageID = extractJsonValue(jsonString, "messageID");
        msg.messageHash = extractJsonValue(jsonString, "messageHash");
        msg.sender = extractJsonValue(jsonString, "sender");
        msg.recipient = extractJsonValue(jsonString, "recipient");
        msg.messageText = extractJsonValue(jsonString, "messageText");
        msg.flag = extractJsonValue(jsonString, "flag");

        return msg;
    }

    private static String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\": \"";
        int startIndex = json.indexOf(searchKey);

        if (startIndex == -1) {
            return "";
        }

        startIndex += searchKey.length();
        int endIndex = json.indexOf("\"", startIndex);

        if (endIndex == -1) {
            return "";
        }

        return json.substring(startIndex, endIndex);
    }

    public static ArrayList<Message> getSentMessagesArray() {
        return sentMessagesArray;
    }

    public static ArrayList<Message> getDisregardedMessagesArray() {
        return disregardedMessagesArray;
    }

    public static ArrayList<Message> getStoredMessagesArray() {
        return storedMessagesArray;
    }

    public static ArrayList<String> getMessageHashesArray() {
        return messageHashesArray;
    }

    public static ArrayList<String> getMessageIDsArray() {
        return messageIDsArray;
    }

    public static void displayStoredMessagesSenderAndRecipient() {
        System.out.println("\n===== STORED MESSAGES - SENDER & RECIPIENT =====");

        for (Message msg : storedMessagesArray) {
            if (msg.flag.equals("Stored")) {
                System.out.println("Sender: " + msg.sender + " | Recipient: " + msg.recipient);
            }
        }

        ArrayList<String> jsonMessages = loadStoredMessagesFromJSON();

        for (String jsonMsg : jsonMessages) {
            StoredMessage msg = parseJSONToStoredMessage(jsonMsg);
            if (msg.flag.equals("Stored")) {
                System.out.println("Sender: " + msg.sender + " | Recipient: " + msg.recipient);
            }
        }
    }

    public static String getLongestStoredMessage() {
        ArrayList<String> jsonMessages = loadStoredMessagesFromJSON();
        String longestMessage = "";

        for (String jsonMsg : jsonMessages) {
            StoredMessage msg = parseJSONToStoredMessage(jsonMsg);

            if (msg.flag.equals("Stored") && msg.messageText.length() > longestMessage.length()) {
                longestMessage = msg.messageText;
            }
        }

        for (Message msg : storedMessagesArray) {
            if (msg.flag.equals("Stored") && msg.messageText.length() > longestMessage.length()) {
                longestMessage = msg.messageText;
            }
        }

        return longestMessage.isEmpty() ? "No stored messages found." : longestMessage;
    }

    public static String searchByMessageID(String messageID) {
        ArrayList<String> jsonMessages = loadStoredMessagesFromJSON();

        for (String jsonMsg : jsonMessages) {
            StoredMessage msg = parseJSONToStoredMessage(jsonMsg);

            if (msg.messageID.equals(messageID)) {
                return "Recipient: " + msg.recipient + "\nMessage: " + msg.messageText;
            }
        }

        for (Message msg : storedMessagesArray) {
            if (msg.messageID.equals(messageID)) {
                return "Recipient: " + msg.recipient + "\nMessage: " + msg.messageText;
            }
        }

        return "Message ID not found.";
    }

    public static ArrayList<String> searchByRecipient(String recipientNumber) {
        // Use LinkedHashSet to preserve insertion order and remove duplicates
        Set<String> resultsSet = new LinkedHashSet<>();
        ArrayList<String> jsonMessages = loadStoredMessagesFromJSON();

        for (String jsonMsg : jsonMessages) {
            StoredMessage msg = parseJSONToStoredMessage(jsonMsg);

            if (msg.recipient.equals(recipientNumber)) {
                resultsSet.add(msg.messageText);
            }
        }

        for (Message msg : storedMessagesArray) {
            if (msg.recipient.equals(recipientNumber)) {
                resultsSet.add(msg.messageText);
            }
        }

        return new ArrayList<>(resultsSet);
    }

    public static String deleteByMessageHash(String messageHash) {
        ArrayList<String> jsonMessages = loadStoredMessagesFromJSON();
        boolean deleted = false;
        ArrayList<String> updatedMessages = new ArrayList<>();

        for (String jsonMsg : jsonMessages) {
            StoredMessage msg = parseJSONToStoredMessage(jsonMsg);

            if (msg.messageHash.equals(messageHash)) {
                deleted = true;
                for (int i = 0; i < storedMessagesArray.size(); i++) {
                    if (storedMessagesArray.get(i).messageHash.equals(messageHash)) {
                        storedMessagesArray.remove(i);
                        break;
                    }
                }
            } else {
                updatedMessages.add(jsonMsg);
            }
        }

        try {
            FileWriter writer = new FileWriter("storedMessages.json");
            writer.write("[\n");

            for (int i = 0; i < updatedMessages.size(); i++) {
                writer.write(updatedMessages.get(i));

                if (i < updatedMessages.size() - 1) {
                    writer.write(",\n");
                } else {
                    writer.write("\n");
                }
            }

            writer.write("]");
            writer.close();

        } catch (IOException e) {
            return "Error saving after deletion.";
        }

        for (int i = 0; i < storedMessagesArray.size(); i++) {
            if (storedMessagesArray.get(i).messageHash.equals(messageHash)) {
                storedMessagesArray.remove(i);
                deleted = true;
                break;
            }
        }

        for (int i = 0; i < messageHashesArray.size(); i++) {
            if (messageHashesArray.get(i).equals(messageHash)) {
                messageHashesArray.remove(i);
                break;
            }
        }

        return deleted ? "Message successfully deleted." : "Message hash not found.";
    }

    public static void displayFullReport() {
        System.out.println("\n===== FULL STORED MESSAGES REPORT =====");
        System.out.println("\n--- All Stored Messages ---");

        ArrayList<String> jsonMessages = loadStoredMessagesFromJSON();

        if (jsonMessages.isEmpty() && storedMessagesArray.isEmpty()) {
            System.out.println("No stored messages found.");
            return;
        }

        int reportCount = 0;
        
        for (String jsonMsg : jsonMessages) {
            StoredMessage msg = parseJSONToStoredMessage(jsonMsg);
            if (msg.flag.equals("Stored")) {
                System.out.println("\n--- Message " + (++reportCount) + " ---");
                System.out.println("Message Hash: " + msg.messageHash);
                System.out.println("Recipient: " + msg.recipient);
                System.out.println("Message: " + msg.messageText);
            }
        }

        for (Message msg : storedMessagesArray) {
            if (msg.flag.equals("Stored")) {
                System.out.println("\n--- Message " + (++reportCount) + " ---");
                System.out.println("Message Hash: " + msg.messageHash);
                System.out.println("Recipient: " + msg.recipient);
                System.out.println("Message: " + msg.messageText);
            }
        }
    }

    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    public static int returnTotalStored() {
        return totalMessagesStored;
    }

    public static int returnTotalDisregarded() {
        return totalMessagesDisregarded;
    }
    
    public static void resetCounters() {
        totalMessagesSent = 0;
        totalMessagesStored = 0;
        totalMessagesDisregarded = 0;
    }
    
    public static void clearAllArrays() {
        sentMessagesArray.clear();
        disregardedMessagesArray.clear();
        storedMessagesArray.clear();
        messageHashesArray.clear();
        messageIDsArray.clear();
    }
    
    public static void clearJSONFile() {
        try {
            FileWriter writer = new FileWriter("storedMessages.json");
            writer.write("[]");
            writer.close();
        } catch (IOException e) {
            System.out.println("Error clearing JSON file: " + e.getMessage());
        }
    }

    public String getMessageHash() {
        return messageHash;
    }

    public String getMessageID() {
        return messageID;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getSender() {
        return sender;
    }

    public String getFlag() {
        return flag;
    }
}

class StoredMessage {
    String messageID = "";
    String messageHash = "";
    String sender = "";
    String recipient = "";
    String messageText = "";
    String flag = "";
}