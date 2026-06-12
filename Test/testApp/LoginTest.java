package testApp;

import mainApp.Login;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoginTest {

    // ===== REGISTER USER TESTS =====
    @Test
    public void testUsernameIsCorrectlyFormatted() {
        Login account = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertEquals("Username successfully captured.\nPassword successfully captured.\nCell phone number successfully added.", account.registerUser());
    }

    @Test
    public void testUsernameIsIncorrectlyFormatted() {
        Login account = new Login("Kyle", "Smith", "kyle!!!!!!", "Ch&&sec@ke99!", "+27838968976");
        assertEquals("Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.", account.registerUser());
    }

    @Test
    public void testPasswordMeetsComplexityRequirements() {
        Login account = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertEquals("Username successfully captured.\nPassword successfully captured.\nCell phone number successfully added.", account.registerUser());
    }

    @Test
    public void testPasswordDoesNotMeetComplexityRequirements() {
        Login account = new Login("Kyle", "Smith", "kyl_1", "password", "+27838968976");
        assertEquals("Password is not correctly formatted, please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.", account.registerUser());
    }

    @Test
    public void testCellPhoneNumberCorrectlyFormatted() {
        Login account = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertEquals("Username successfully captured.\nPassword successfully captured.\nCell phone number successfully added.", account.registerUser());
    }

    @Test
    public void testCellPhoneNumberIncorrectlyFormatted() {
        Login account = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "08966553");
        assertEquals("Cell phone number incorrectly formatted or does not contain international code.", account.registerUser());
    }

    // ===== LOGIN TESTS =====
    @Test
    public void testLoginSuccessful() {
        Login account = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(account.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    public void testLoginFailed() {
        Login account = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertFalse(account.loginUser("wrongUser", "wrongPass"));
    }

    // ===== BOOLEAN VALIDATION TESTS =====
    @Test
    public void testUsernameCorrectlyFormattedBoolean() {
        Login account = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(account.checkUserName());
    }

    @Test
    public void testUsernameIncorrectlyFormattedBoolean() {
        Login account = new Login("Kyle", "Smith", "kyle!!!!!!", "Ch&&sec@ke99!", "+27838968976");
        assertFalse(account.checkUserName());
    }

    @Test
    public void testPasswordMeetsComplexityRequirementsBoolean() {
        Login account = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(account.checkPasswordComplexity());
    }

    @Test
    public void testPasswordDoesNotMeetComplexityRequirementsBoolean() {
        Login account = new Login("Kyle", "Smith", "kyl_1", "password", "+27838968976");
        assertFalse(account.checkPasswordComplexity());
    }

    @Test
    public void testCellPhoneNumberCorrectlyFormattedBoolean() {
        Login account = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(account.checkCellPhoneNumber());
    }

    @Test
    public void testCellPhoneNumberIncorrectlyFormattedBoolean() {
        Login account = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "08966553");
        assertFalse(account.checkCellPhoneNumber());
    }
    
    @Test
    public void testReturnLoginStatusSuccess() {
        Login account = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertEquals("Welcome Kyle, Smith it is great to see you again.", account.returnLoginStatus("kyl_1", "Ch&&sec@ke99!"));
    }
    
    @Test
    public void testReturnLoginStatusFailure() {
        Login account = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertEquals("Username or password incorrect, please try again.", account.returnLoginStatus("wrong", "wrong"));
    }
}