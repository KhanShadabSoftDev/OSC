package com.osc.userdataservice.grpcservice;

import com.grpc.user.*;
import com.osc.userdataservice.dbservice.UserDbService;
import com.osc.userdataservice.entity.User;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@GrpcService
public class UserDataService extends UserDataServiceGrpc.UserDataServiceImplBase {

    private static final Logger logger = LogManager.getLogger(UserDataService.class);

    private final UserDbService dbService;

    public UserDataService(UserDbService dbService) {
        this.dbService = dbService;
    }

    @Override
    public void verifyEmailAddressIsUnique(UniqueEmailRequest request, StreamObserver<UniqueEmailResponse> responseObserver) {
        try {
            String email = request.getEmail();
            logger.info("Checking if email is unique: {}", email);
            boolean uniqueEmail = dbService.checkUniqueEmail(email);

            UniqueEmailResponse response = UniqueEmailResponse.newBuilder()
                    .setIsUnique(uniqueEmail)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

            logger.info("Email uniqueness check completed successfully for email: {}", email);

        } catch (StatusRuntimeException ex) {
            logger.error("Failed to check email uniqueness for email: {}", request.getEmail(), ex);
            responseObserver.onError(Status.INTERNAL.withDescription("Failed to check email uniqueness").withCause(ex).asRuntimeException());
        }
    }

    @Override
    public void registerUser(RegisterUserRequest request, StreamObserver<RegisterUserResponse> responseObserver) {
        try {
            String userId = request.getUserId();
            String name = request.getName();
            String emailId = request.getEmail();
            String password = request.getPassword();
            String mobileNumber = request.getMobileNumber();
            String dateOfBirth = request.getDob();

            logger.info("Registering user with email: {}", emailId);

            User user = new User(userId, name, emailId, password, mobileNumber, dateOfBirth);
            System.out.println(user);

            boolean isRegistered = dbService.save(user);

            RegisterUserResponse response = RegisterUserResponse.newBuilder()
                    .setSuccess(isRegistered)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            logger.info("User registration completed for email: {} with status: {}", emailId, isRegistered ? "Success" : "Failure");

        } catch (StatusRuntimeException ex) {
            logger.error("Failed to register user with email: {}", request.getEmail(), ex);
            responseObserver.onError(Status.INTERNAL.withDescription("Failed to register user").withCause(ex).asRuntimeException());
        } catch (Exception e) {
            logger.error("An unexpected error occurred during user registration with email: {}", request.getEmail(), e);
            responseObserver.onError(Status.INTERNAL.withDescription("An unexpected error occurred").withCause(e).asRuntimeException());
        }
    }

    @Override
    public void validEmailRequest(ValidEmailRequest request, StreamObserver<ValidEmailResponse> responseObserver) {
        try {
            String email = request.getEmail();
            logger.info("Validating email: {}", email);

            User user = dbService.checkValidEmail(email);

            ValidEmailResponse response = ValidEmailResponse.newBuilder()
                    .setUserId(user != null ? user.getUserId() : "")
                    .setIsValid(user != null)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            logger.info("Email validation completed for email: {} with result: {}", email, user != null ? "Valid" : "Invalid");

        } catch (StatusRuntimeException ex) {
            logger.error("Failed to validate email: {}", request.getEmail(), ex);
            responseObserver.onError(Status.INTERNAL.withDescription("Failed to validate email").withCause(ex).asRuntimeException());
        }
    }

    @Override
    public void verifyCredentials(VerifyCredentialsRequest request, StreamObserver<VerifyCredentialsResponse> responseObserver) {
        try {
            String userId = request.getUserId();
            logger.info("Verifying credentials for userId: {}", userId);

            User user = dbService.verifyCredential(userId);

            if (user != null) {
                VerifyCredentialsResponse response = VerifyCredentialsResponse.newBuilder()
                        .setPassword(user.getPassword())
                        .setName(user.getName())
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();

                logger.info("Credentials verification successful for userId: {}", userId);
            } else {
                responseObserver.onError(Status.NOT_FOUND.withDescription("User not found").asRuntimeException());
                logger.warn("User not found for userId: {}", userId);
            }
        } catch (StatusRuntimeException ex) {
            logger.error("Failed to verify credentials for userId: {}", request.getUserId(), ex);
            responseObserver.onError(Status.INTERNAL.withDescription("Failed to verify credentials").withCause(ex).asRuntimeException());
        }
    }

    @Override
    public void resetPassword(PasswordRequest request, StreamObserver<PasswordResponse> responseObserver) {
        try {
            String email = request.getEmail();
            String password = request.getPassword();

            logger.info("Resetting password for email: {}", email);

            User user = dbService.resetPassword(email, password);

            PasswordResponse response = PasswordResponse.newBuilder()
                    .setUserId(user != null ? user.getUserId() : "")
                    .setSuccess(user != null)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            logger.info("Password reset completed for email: {} with success: {}", email, user != null);
        } catch (StatusRuntimeException ex) {
            logger.error("Failed to reset password for email: {}", request.getEmail(), ex);
            responseObserver.onError(Status.INTERNAL.withDescription("Failed to reset password").withCause(ex).asRuntimeException());
        }
    }
}
