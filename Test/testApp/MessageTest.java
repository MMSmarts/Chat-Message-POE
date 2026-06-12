package testApp;

import mainApp.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {
    
    private Message testMessage;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    
    @BeforeEach
    public void setUp() {
        // Clear all arrays and reset counters before each test
        Message.clearAllArrays();
        Message.resetCounters();
        Message.clearJSONFile();
        testMessage = new Message("0012345678", 1, "+27718693002", "Test Sender", "Hi Mike, can you join us for dinner tonight?");
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    // ===== MESSAGE LENGTH TESTS =====
    @Test
    public void testMessageLengthSuccess() {
        assertTrue(testMessage.checkMessageLength());
        assertEquals("Message ready to send.", testMessage.checkMessageLengthResult());
    }

    @Test
    public void testMessageLengthFailure() {
        String longMessage = "A".repeat(260);
        Message message = new Message("0012345678", 1, "+27718693002", "Test Sender", longMessage);
        assertFalse(message.checkMessageLength());
        assertTrue(message.checkMessageLengthResult().contains("exceeds 250 characters"));
    }

    // ===== RECIPIENT VALIDATION TESTS =====
    @Test
    public void testRecipientNumberCorrectlyFormatted() {
        assertEquals(1, testMessage.checkRecipientCell());
        assertEquals("Cell phone number successfully captured.", testMessage.checkRecipientCellString());
    }

    @Test
    public void testRecipientNumberIncorrectlyFormatted() {
        Message message = new Message("0012345678", 1, "08575975889", "Test Sender", "Hi Keegan, did you receive the payment?");
        assertEquals(0, message.checkRecipientCell());
        assertTrue(message.checkRecipientCellString().contains("incorrectly formatted"));
    }

    // ===== MESSAGE HASH TESTS (POE Page 7) =====
    @Test
    public void testMessageHashCreatedCorrectly() {
        assertEquals("00:1:HITONIGHT", testMessage.getMessageHash());
    }
    
    @Test
    public void testMessageHashWithSingleWord() {
        Message message = new Message("0012345678", 2, "+27718693002", "Test Sender", "Hello");
        assertEquals("00:2:HELLO", message.getMessageHash());
    }
    
    @Test
    public void testMessageHashWithPunctuation() {
        Message message = new Message("0012345678", 3, "+27718693002", "Test Sender", "Hello world!");
        assertEquals("00:3:HELLOWORLD", message.getMessageHash());
    }

    // ===== MESSAGE ID TESTS =====
    @Test
    public void testMessageIDCreated() {
        assertTrue(testMessage.checkMessageID());
    }
    
    @Test
    public void testMessageIDInvalid() {
        Message message = new Message("123", 1, "+27718693002", "Test Sender", "Test");
        assertFalse(message.checkMessageID());
    }

    // ===== SEND/DISREGARD/STORE TESTS =====
    @Test
    public void testSendMessage() {
        assertEquals("Message successfully sent.", testMessage.sentMessage(1));
    }

    @Test
    public void testDisregardMessage() {
        assertEquals("Press 0 to delete message.", testMessage.sentMessage(2));
    }

    @Test
    public void testStoreMessage() {
        assertEquals("Message successfully stored.", testMessage.sentMessage(3));
    }
    
    @Test
    public void testInvalidOption() {
        assertEquals("Invalid option selected.", testMessage.sentMessage(99));
    }
    
    // ===== PART 3 ARRAY TESTS (POE Page 11-12) =====
    @Test
    public void testSentMessagesArrayCorrectlyPopulated() {
        Message.clearAllArrays();
        
        Message msg1 = new Message("0012345678", 1, "+27834557896", "Developer", "Did you get the cake?");
        Message msg4 = new Message("0012345679", 2, "0838884567", "Developer", "It is dinner time!");
        
        msg1.sentMessage(1);
        msg4.sentMessage(1);
        
        ArrayList<Message> sentMessages = Message.getSentMessagesArray();
        assertTrue(sentMessages.size() >= 2);
        
        boolean hasCake = false;
        boolean hasDinner = false;
        for (Message msg : sentMessages) {
            if (msg.getMessageText().equals("Did you get the cake?")) hasCake = true;
            if (msg.getMessageText().equals("It is dinner time!")) hasDinner = true;
        }
        assertTrue(hasCake && hasDinner);
    }
    
    @Test
    public void testLongestMessage() {
        Message.clearAllArrays();
        
        Message msg1 = new Message("0012345678", 1, "+27834557896", "Test", "Did you get the cake?");
        Message msg2 = new Message("0012345679", 2, "+27838884567", "Test", "Where are you? You are late! I have asked you to be on time.");
        Message msg3 = new Message("0012345680", 3, "+27834484567", "Test", "Yohoooo, I am at your gate.");
        
        msg1.sentMessage(3);
        msg2.sentMessage(3);
        msg3.sentMessage(2);
        
        String longest = Message.getLongestStoredMessage();
        assertTrue(longest.contains("Where are you? You are late!"));
        assertEquals("Where are you? You are late! I have asked you to be on time.", longest);
    }
    
    @Test
    public void testSearchByMessageID() {
        Message.clearAllArrays();
        
        Message msg = new Message("0838884567", 4, "+27838884567", "Developer", "It is dinner time!");
        msg.sentMessage(3);
        
        String result = Message.searchByMessageID("0838884567");
        assertTrue(result.contains("It is dinner time!"));
        assertTrue(result.contains("Recipient: +27838884567"));
    }
    
    @Test
    public void testSearchByRecipient() {
        Message.clearAllArrays();
        
        Message msg1 = new Message("0012345678", 1, "+27838884567", "Test", "Where are you? You are late!");
        Message msg2 = new Message("0012345679", 2, "+27838884567", "Test", "Ok, I am leaving without you.");
        
        msg1.sentMessage(3);
        msg2.sentMessage(3);
        
        ArrayList<String> results = Message.searchByRecipient("+27838884567");
        assertEquals(2, results.size());
        assertTrue(results.get(0).contains("Where are you?"));
        assertTrue(results.get(1).contains("leaving without you"));
    }
    
    @Test
    public void testDeleteByMessageHash() {
        Message.clearAllArrays();
        
        Message msg = new Message("0012345678", 1, "+27838884567", "Test", "Where are you? You are late!");
        msg.sentMessage(3);
        
        String hash = msg.getMessageHash();
        String result = Message.deleteByMessageHash(hash);
        assertEquals("Message successfully deleted.", result);
        
        // Verify it's deleted
        String searchResult = Message.searchByMessageID("0012345678");
        assertEquals("Message ID not found.", searchResult);
    }
    
    @Test
    public void testDisplayReport() {
        Message.clearAllArrays();
        Message.clearJSONFile();
        
        // Create test messages following POE Page 11
        Message msg1 = new Message("1111111111", 1, "+27834557896", "Developer", "Did you get the cake?");
        Message msg2 = new Message("2222222222", 2, "+27838884567", "Developer", "Where are you? You are late! I have asked you to be on time.");
        
        msg1.sentMessage(1);  // Sent message (should NOT appear in stored report)
        msg2.sentMessage(3);  // Stored message (should appear in stored report)
        
        // Capture the report output
        ByteArrayOutputStream reportOutput = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(reportOutput));
        
        Message.displayFullReport();
        
        System.setOut(originalOut);
        String reportContent = reportOutput.toString();
        
        // Verify report contains stored message but not sent message
        assertTrue(reportContent.contains("Where are you? You are late! I have asked you to be on time."));
        assertFalse(reportContent.contains("Did you get the cake?"));
        
        // Verify report contains required fields: Message Hash, Recipient, Message
        assertTrue(reportContent.contains("Message Hash:"));
        assertTrue(reportContent.contains("Recipient:"));
        assertTrue(reportContent.contains("Message:"));
    }
    
    @Test
    public void testPopulateTestData() {
        Message.clearAllArrays();
        Message.resetCounters();
        
        Message.populateTestData();
        
        // Verify test data was populated correctly per POE Page 11
        ArrayList<Message> sentMessages = Message.getSentMessagesArray();
        ArrayList<Message> storedMessages = Message.getStoredMessagesArray();
        ArrayList<Message> disregardedMessages = Message.getDisregardedMessagesArray();
        
        // Should have 2 sent messages
        int sentCount = 0;
        for (Message msg : sentMessages) {
            if (msg.getFlag().equals("Sent")) sentCount++;
        }
        assertEquals(2, sentCount);
        
        // Should have 2 stored messages
        int storedCount = 0;
        for (Message msg : storedMessages) {
            if (msg.getFlag().equals("Stored")) storedCount++;
        }
        assertEquals(2, storedCount);
        
        // Should have 1 disregarded message
        int disregardedCount = 0;
        for (Message msg : disregardedMessages) {
            if (msg.getFlag().equals("Disregarded")) disregardedCount++;
        }
        assertEquals(1, disregardedCount);
        
        // Verify specific messages exist
        boolean hasCakeMessage = false;
        boolean hasDinnerMessage = false;
        for (Message msg : sentMessages) {
            if (msg.getMessageText().equals("Did you get the cake?")) hasCakeMessage = true;
            if (msg.getMessageText().equals("It is dinner time!")) hasDinnerMessage = true;
        }
        assertTrue(hasCakeMessage && hasDinnerMessage);
    }
    
    @Test
    public void testMessagePrintFormat() {
        String printed = testMessage.printMessage();
        assertTrue(printed.contains("Message ID:"));
        assertTrue(printed.contains("Message Hash:"));
        assertTrue(printed.contains("Sender:"));
        assertTrue(printed.contains("Recipient:"));
        assertTrue(printed.contains("Message:"));
    }
    
    @Test
    public void testLoadStoredMessagesIntoArray() {
        Message.clearAllArrays();
        Message.clearJSONFile();
        
        // Create and store a message
        Message msg = new Message("9876543210", 1, "+27718693002", "Test", "Test message for loading");
        msg.sentMessage(3);
        
        // Clear array to simulate fresh start
        Message.clearAllArrays();
        Message.resetCounters();
        
        // Load from JSON
        Message.loadStoredMessagesIntoArray();
        
        // Verify message was loaded
        ArrayList<Message> storedMessages = Message.getStoredMessagesArray();
        boolean found = false;
        for (Message m : storedMessages) {
            if (m.getMessageText().equals("Test message for loading")) {
                found = true;
                break;
            }
        }
        assertTrue(found, "Message should persist in JSON file between sessions");
    }
    
    @Test
    public void testDisplayStoredMessagesSenderAndRecipient() {
        Message.clearAllArrays();
        
        Message msg1 = new Message("1111111111", 1, "+27834557896", "Alice", "Hello");
        Message msg2 = new Message("2222222222", 2, "+27838884567", "Bob", "World");
        
        msg1.sentMessage(3); // Stored
        msg2.sentMessage(3); // Stored
        
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output));
        
        Message.displayStoredMessagesSenderAndRecipient();
        
        System.setOut(originalOut);
        String outputContent = output.toString();
        
        assertTrue(outputContent.contains("Sender: Alice | Recipient: +27834557896"));
        assertTrue(outputContent.contains("Sender: Bob | Recipient: +27838884567"));
    }
    
    @Test
    public void testSearchByMessageIDNotFound() {
        Message.clearAllArrays();
        String result = Message.searchByMessageID("9999999999");
        assertEquals("Message ID not found.", result);
    }
    
    @Test
    public void testDeleteByMessageHashNotFound() {
        Message.clearAllArrays();
        String result = Message.deleteByMessageHash("99:9:NOHASH");
        assertEquals("Message hash not found.", result);
    }
    
    @Test
    public void testGetLongestStoredMessageEmpty() {
        Message.clearAllArrays();
        String result = Message.getLongestStoredMessage();
        assertEquals("No stored messages found.", result);
    }
}