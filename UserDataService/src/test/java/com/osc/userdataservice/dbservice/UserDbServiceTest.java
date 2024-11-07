package com.osc.userdataservice.dbservice;

import com.osc.userdataservice.entity.User;
import com.osc.userdataservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDbServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDbService userDbService;

    private static final String EMAIL_ID = "user@example.com";
    private static final String USER_ID = "User0001";
    private static final String PASSWORD = "newPassword";

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(USER_ID, "Khan Shadab", EMAIL_ID, PASSWORD, "1234567890", "2000-01-01");
    }

    @Test
    void checkUniqueEmail_ShouldReturnTrue_WhenEmailIsUnique() {
        when(userRepository.findByEmailId(EMAIL_ID)).thenReturn(null);

        boolean result = userDbService.checkUniqueEmail(EMAIL_ID);

        assertTrue(result);
    }

    @Test
    void checkUniqueEmail_ShouldReturnFalse_WhenEmailIsNotUnique() {
        when(userRepository.findByEmailId(EMAIL_ID)).thenReturn(user);

        boolean result = userDbService.checkUniqueEmail(EMAIL_ID);

        assertFalse(result);
    }

    @Test
    void save_ShouldReturnTrue_WhenUserIsSavedSuccessfully() {
        when(userRepository.save(user)).thenReturn(user);

        boolean result = userDbService.save(user);

        assertTrue(result);
    }

    @Test
    void checkValidEmail_ShouldReturnUser_WhenEmailExists() {
        when(userRepository.findByEmailId(EMAIL_ID)).thenReturn(user);

        User result = userDbService.checkValidEmail(EMAIL_ID);

        assertNotNull(result);
        assertEquals(EMAIL_ID, result.getEmailId());
    }

    @Test
    void checkValidEmail_ShouldReturnNull_WhenEmailDoesNotExist() {
        when(userRepository.findByEmailId(EMAIL_ID)).thenReturn(null);

        User result = userDbService.checkValidEmail(EMAIL_ID);

        assertNull(result);
    }

    @Test
    void verifyCredential_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        User result = userDbService.verifyCredential(USER_ID);

        assertNotNull(result);
        assertEquals(USER_ID, result.getUserId());
    }

    @Test
    void verifyCredential_ShouldReturnNull_WhenUserDoesNotExist() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        User result = userDbService.verifyCredential(USER_ID);

        assertNull(result);
    }

    @Test
    void resetPassword_ShouldReturnUpdatedUser_WhenUserExists() {
        when(userRepository.findByEmailId(EMAIL_ID)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        User result = userDbService.resetPassword(EMAIL_ID, PASSWORD);

        assertNotNull(result);
        assertEquals(PASSWORD, result.getPassword());
    }

    @Test
    void resetPassword_ShouldReturnNull_WhenUserDoesNotExist() {
        when(userRepository.findByEmailId(EMAIL_ID)).thenReturn(null);

        User result = userDbService.resetPassword(EMAIL_ID, PASSWORD);

        assertNull(result);
    }
}
