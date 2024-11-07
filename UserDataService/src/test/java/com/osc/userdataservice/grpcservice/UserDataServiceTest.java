package com.osc.userdataservice.grpcservice;


import com.grpc.user.*;
import com.osc.userdataservice.dbservice.UserDbService;
import com.osc.userdataservice.entity.User;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDataServiceTest {

    @Mock
    private UserDbService dbService;

    @Mock
    private StreamObserver<UniqueEmailResponse> uniqueEmailResponseObserver;

    @Mock
    private StreamObserver<RegisterUserResponse> registerUserResponseObserver;

    @Mock
    private StreamObserver<ValidEmailResponse> validEmailResponseObserver;

    @Mock
    private StreamObserver<VerifyCredentialsResponse> verifyCredentialsResponseObserver;

    @Mock
    private StreamObserver<PasswordResponse> passwordResponseObserver;

    @InjectMocks
    private UserDataService userDataService;

    @Captor
    private ArgumentCaptor<UniqueEmailResponse> uniqueEmailResponseCaptor;

    @Captor
    private ArgumentCaptor<RegisterUserResponse> registerUserResponseCaptor;

    @Captor
    private ArgumentCaptor<ValidEmailResponse> validEmailResponseCaptor;

    @Captor
    private ArgumentCaptor<VerifyCredentialsResponse> verifyCredentialsResponseCaptor;

    @Captor
    private ArgumentCaptor<PasswordResponse> passwordResponseCaptor;

    private static final String EMAIL_ID = "test@example.com";
    private static final String USER_ID = "User0001";

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(USER_ID, "Test User", EMAIL_ID, "password", "1234567890", "1990-01-01");
    }

    @Test
    void verifyEmailAddressIsUnique_ShouldReturnUniqueResponse() {
        // Arrange
        when(dbService.checkUniqueEmail(EMAIL_ID)).thenReturn(true);

        UniqueEmailRequest request = UniqueEmailRequest.newBuilder().setEmail(EMAIL_ID).build();

        // Act
        userDataService.verifyEmailAddressIsUnique(request, uniqueEmailResponseObserver);

        // Assert
        verify(uniqueEmailResponseObserver).onNext(uniqueEmailResponseCaptor.capture());
        verify(uniqueEmailResponseObserver).onCompleted();

        UniqueEmailResponse response = uniqueEmailResponseCaptor.getValue();
        assertTrue(response.getIsUnique());
    }

    @Test
    void registerUser_ShouldRegisterUserSuccessfully() {
        // Arrange
        when(dbService.save(any(User.class))).thenReturn(true);

        RegisterUserRequest request = RegisterUserRequest.newBuilder()
                .setUserId(USER_ID)
                .setName(user.getName())
                .setEmail(user.getEmailId())
                .setPassword(user.getPassword())
                .setMobileNumber(user.getMobileNumber())
                .setDob(user.getDateOfBirth())
                .build();

        // Act
        userDataService.registerUser(request, registerUserResponseObserver);

        // Assert
        verify(registerUserResponseObserver).onNext(registerUserResponseCaptor.capture());
        verify(registerUserResponseObserver).onCompleted();

        RegisterUserResponse response = registerUserResponseCaptor.getValue();
        assertTrue(response.getSuccess());
    }

    @Test
    void validEmailRequest_ShouldReturnValidEmailResponse() {
        // Arrange
        when(dbService.checkValidEmail(EMAIL_ID)).thenReturn(user);

        ValidEmailRequest request = ValidEmailRequest.newBuilder().setEmail(EMAIL_ID).build();

        // Act
        userDataService.validEmailRequest(request, validEmailResponseObserver);

        // Assert
        verify(validEmailResponseObserver).onNext(validEmailResponseCaptor.capture());
        verify(validEmailResponseObserver).onCompleted();

        ValidEmailResponse response = validEmailResponseCaptor.getValue();
        assertTrue(response.getIsValid());
        assertEquals(USER_ID, response.getUserId());
    }

    @Test
    void verifyCredentials_ShouldReturnCredentialsResponse() {
        // Arrange
        when(dbService.verifyCredential(USER_ID)).thenReturn(user);

        VerifyCredentialsRequest request = VerifyCredentialsRequest.newBuilder().setUserId(USER_ID).build();

        // Act
        userDataService.verifyCredentials(request, verifyCredentialsResponseObserver);

        // Assert
        verify(verifyCredentialsResponseObserver).onNext(verifyCredentialsResponseCaptor.capture());
        verify(verifyCredentialsResponseObserver).onCompleted();

        VerifyCredentialsResponse response = verifyCredentialsResponseCaptor.getValue();
        assertEquals(user.getPassword(), response.getPassword());
        assertEquals(user.getName(), response.getName());
    }

    @Test
    void resetPassword_ShouldResetPasswordSuccessfully() {
        // Arrange
        when(dbService.resetPassword(EMAIL_ID, "newPassword")).thenReturn(user);

        PasswordRequest request = PasswordRequest.newBuilder()
                .setEmail(EMAIL_ID)
                .setPassword("newPassword")
                .build();

        // Act
        userDataService.resetPassword(request, passwordResponseObserver);

        // Assert
        verify(passwordResponseObserver).onNext(passwordResponseCaptor.capture());
        verify(passwordResponseObserver).onCompleted();

        PasswordResponse response = passwordResponseCaptor.getValue();
        assertTrue(response.getSuccess());
        assertEquals(USER_ID, response.getUserId());
    }

    @Test
    void resetPassword_ShouldHandleGrpcException() {
        // Arrange
        when(dbService.resetPassword(EMAIL_ID, "newPassword")).thenThrow(new StatusRuntimeException(Status.INTERNAL));

        PasswordRequest request = PasswordRequest.newBuilder()
                .setEmail(EMAIL_ID)
                .setPassword("newPassword")
                .build();

        // Act
        userDataService.resetPassword(request, passwordResponseObserver);

        // Assert
        verify(passwordResponseObserver, never()).onNext(any());
        verify(passwordResponseObserver).onError(any(StatusRuntimeException.class));
    }
}
