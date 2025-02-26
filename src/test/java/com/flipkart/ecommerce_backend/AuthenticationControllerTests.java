package com.flipkart.ecommerce_backend;

import com.flipkart.ecommerce_backend.api.models.RegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationControllerTests {
    @Autowired
    TestRestTemplate testRestTemplate;
    String uniqueUsername = "user_" + UUID.randomUUID().toString().substring(0, 8);
    String uniqueEmail = "user_" + UUID.randomUUID().toString().substring(0, 8) + "@gmail.com";

    @Test
    void testRegisterUserBlankUsernameFails() {
        RegistrationRequest request = new RegistrationRequest("", "email@example.com", "weak", "First", "Last");
        ResponseEntity<Map> response = testRestTemplate.postForEntity("/api/auth/registeruser", request, Map.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map errors = response.getBody();
        assertNotNull(errors);
        assertTrue(errors.containsKey("user_name"));
        assertEquals("User Name cannot be blank", errors.get("user_name"));
    }
    @Test
    void testRegisterUserShortUsernameFails() {
        RegistrationRequest request = new RegistrationRequest("a", uniqueEmail, "weak", "First", "Last");
        ResponseEntity<Map> response = testRestTemplate.postForEntity("/api/auth/registeruser", request, Map.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map errors = response.getBody();
        assertNotNull(errors);
        assertTrue(errors.containsKey("user_name"));
        assertEquals("User Name must be between 3 and 20 characters", errors.get("user_name"));
    }
    @Test
    void testRegisterUserLongUsernameFails() {
        RegistrationRequest request = new RegistrationRequest("thisusernameisverylongandshouldnotbeaccepted", uniqueEmail, "weak", "First", "Last");
        ResponseEntity<Map> response = testRestTemplate.postForEntity("/api/auth/registeruser", request, Map.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map errors = response.getBody();
        assertNotNull(errors);
        assertTrue(errors.containsKey("user_name"));
        assertEquals("User Name must be between 3 and 20 characters", errors.get("user_name"));
    }
    @Test
    void testRegisterUserInvalidUsernameFails() {
        RegistrationRequest request = new RegistrationRequest("user name", uniqueEmail, "weak", "First", "Last");
        ResponseEntity<Map> response = testRestTemplate.postForEntity("/api/auth/registeruser", request, Map.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map errors = response.getBody();
        assertNotNull(errors);
        assertTrue(errors.containsKey("user_name"));
        assertEquals("User Name can only contain letters, numbers, and underscores", errors.get("user_name"));
    }
    @Test
    void testRegisterUserInvalidEmailFails() {
        RegistrationRequest request = new RegistrationRequest("username", "email", "weak", "First", "Last");
        ResponseEntity<Map> response = testRestTemplate.postForEntity("/api/auth/registeruser", request, Map.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map errors = response.getBody();
        assertNotNull(errors);
        assertTrue(errors.containsKey("email"));
        assertEquals("Email should be valid", errors.get("email"));
    }
    @Test
    void testRegisterUserShortPasswordFails() {
        RegistrationRequest request = new RegistrationRequest(uniqueUsername, uniqueEmail, "We@k1", "First", "Last");
        ResponseEntity<Map> response = testRestTemplate.postForEntity("/api/auth/registeruser", request, Map.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map errors = response.getBody();
        assertNotNull(errors);
        assertTrue(errors.containsKey("password"));
        assertEquals("Password is weak,it must contain at least 8 characters, include uppercase, lowercase, numbers, and special characters", errors.get("password"));
    }
    @Test
    void testRegisterUserLongPasswordFails() {
        // 101 characters
        RegistrationRequest request = new RegistrationRequest(uniqueUsername, uniqueEmail, "Bbbbb@123".repeat(89), "First", "Last");
        ResponseEntity<Map> response = testRestTemplate.postForEntity("/api/auth/registeruser", request, Map.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map errors = response.getBody();
        assertNotNull(errors);
        assertTrue(errors.containsKey("password"));
        assertEquals("Password must be between 8 and 100 characters", errors.get("password"));
    }
    @Test
    void testRegisterUserExistingUsernameFails() {
        RegistrationRequest request = new RegistrationRequest("username", uniqueEmail, "Password@123", "First", "Last");
        ResponseEntity<Map> response = testRestTemplate.postForEntity("/api/auth/registeruser", request, Map.class);
        System.out.println(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Map errors = response.getBody();
        assertNotNull(errors);
        assertTrue(errors.containsKey("message"));
        assertEquals("Username already exists.", errors.get("message"));
    }
    @Test
    void testRegisterUserExistingEmailFails() {
        RegistrationRequest request = new RegistrationRequest(uniqueUsername, "abc@gmail.com", "Password@123", "First", "Last");
        ResponseEntity<Map> response = testRestTemplate.postForEntity("/api/auth/registeruser", request, Map.class);
        System.out.println(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Map errors = response.getBody();
        assertNotNull(errors);
        assertTrue(errors.containsKey("message"));
        assertEquals("Email address already registered.", errors.get("message"));
    }

    @Test void testRegisterUserBlankEmailFails() {
        RegistrationRequest request = new RegistrationRequest("username", "", "weak", "First", "Last");
        ResponseEntity<Map> response = testRestTemplate.postForEntity("/api/auth/registeruser", request, Map.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map errors = response.getBody();
        assertNotNull(errors);
        assertTrue(errors.containsKey("email"));
        assertEquals("Email cannot be blank", errors.get("email"));
    }

    @Test
    void testRegisterUserBlankPasswordFails() {
        RegistrationRequest request = new RegistrationRequest(uniqueUsername, uniqueEmail, "", "First", "Last");
        ResponseEntity<Map> response = testRestTemplate.postForEntity("/api/auth/registeruser", request, Map.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map errors = response.getBody();
        assertNotNull(errors);
        assertTrue(errors.containsKey("password"));
        assertEquals("Password cannot be blank", errors.get("password"));
    }
    @Test
    void testRegisterUserBlankFirstNameFails() {
        RegistrationRequest request = new RegistrationRequest(uniqueUsername, uniqueEmail, "password", "", "Last");
        ResponseEntity<Map> response = testRestTemplate.postForEntity("/api/auth/registeruser", request, Map.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map errors = response.getBody();
        assertNotNull(errors);
        assertTrue(errors.containsKey("first_name"));
        assertEquals("First Name cannot be blank", errors.get("first_name"));
    }
    @Test
    void testRegisterUserBlankLastNameFails() {
        RegistrationRequest request = new RegistrationRequest(uniqueUsername, uniqueEmail, "Password@123", "First", "");
        ResponseEntity<Map> response = testRestTemplate.postForEntity("/api/auth/registeruser", request, Map.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map errors = response.getBody();
        assertNotNull(errors);
        assertTrue(errors.containsKey("last_name"));
        assertEquals("Last Name cannot be blank", errors.get("last_name"));
    }
    @Test
    void testRegisterUser_success() {
        RegistrationRequest request = new RegistrationRequest(uniqueUsername, uniqueEmail, "Password@123", "First", "Last");
        ResponseEntity<Map> response = testRestTemplate.postForEntity("/api/auth/registeruser", request, Map.class);
        System.out.println(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        Map body = response.getBody();
        assertNotNull(body);
        assertTrue(body.containsKey("message"));
        assertEquals("Registration successful. Please check your email to verify your account.", body.get("message"));
    }
    @Test
    void testRegisterUser_success_pendingVerification() {
        RegistrationRequest request = new RegistrationRequest(uniqueUsername, uniqueEmail, "Password@123", "First", "Last");
        ResponseEntity<Map> response = testRestTemplate.postForEntity("/api/auth/registeruser", request, Map.class);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        Map body = response.getBody();
        assertNotNull(body);
        assertTrue(body.containsKey("status"));
        assertEquals("PENDING_VERIFICATION", body.get("status"));
    }
    @Test
    void testRegisterUser_success_message() {
        RegistrationRequest request = new RegistrationRequest(uniqueUsername, uniqueEmail, "Password@123", "First", "Last");
        ResponseEntity<Map> response = testRestTemplate.postForEntity("/api/auth/registeruser", request, Map.class);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        Map body = response.getBody();
        assertNotNull(body);
        assertTrue(body.containsKey("message"));
        assertEquals("Registration successful. Please check your email to verify your account.", body.get("message"));
    }
    @Test
    void testRegisterUser_success_email() {
        RegistrationRequest request = new RegistrationRequest(uniqueUsername, uniqueEmail, "Password@123", "First", "Last");
        ResponseEntity<Map> response = testRestTemplate.postForEntity("/api/auth/registeruser", request, Map.class);
        System.out.println(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        Map body = response.getBody();
        assertNotNull(body);
        assertTrue(body.containsKey("email"));
        assertEquals(uniqueEmail, body.get("email"));
    }
    @Test
    void testRegisterUser_success_password() {
        RegistrationRequest request = new RegistrationRequest(uniqueUsername, uniqueEmail, "Password@123!", "First", "Last");
        ResponseEntity<Map> response = testRestTemplate.postForEntity("/api/auth/registeruser", request, Map.class);
        System.out.println(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        Map body = response.getBody();
        assertNotNull(body);
        assertTrue(body.containsKey("email"));
        assertEquals(uniqueEmail, body.get("email"));
        assertTrue(body.containsKey("message"));
        assertEquals("Registration successful. Please check your email to verify your account.", body.get("message"));
        assertEquals("PENDING_VERIFICATION", body.get("status"));
    }
    @Test
    void testRegisterUser_success_password_weak() {
        RegistrationRequest request = new RegistrationRequest(uniqueUsername, uniqueEmail, "password", "First", "Last");
        ResponseEntity<Map> response = testRestTemplate.postForEntity("/api/auth/registeruser", request, Map.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map body = response.getBody();
        assertNotNull(body);
        assertTrue(body.containsKey("password"));
        assertEquals("Password is weak,it must contain at least 8 characters, include uppercase, lowercase, numbers, and special characters", body.get("password"));
    }
}
