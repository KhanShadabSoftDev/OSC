package com.osc.sessionservice.service.login;

import com.grpc.user.UserDataServiceGrpc.UserDataServiceBlockingStub;
import com.grpc.user.VerifyCredentialsRequest;
import com.grpc.user.VerifyCredentialsResponse;
import com.osc.sessionservice.dto.CredentialDTO;
import com.osc.sessionservice.exception.InvalidPasswordException;
import com.osc.sessionservice.exception.InvalidUserIDException;
import io.grpc.ManagedChannel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class UserCredentialService {

    private final UserDataServiceBlockingStub userDataServiceStub;

    private final ManagedChannel userDataChannel;

    public UserCredentialService(UserDataServiceBlockingStub userDataServiceStub,
                                 @Qualifier("managedChannel1") ManagedChannel userDataChannel) {
        this.userDataServiceStub = userDataServiceStub;
        this.userDataChannel = userDataChannel;
    }

    public String verifyUserCredentials(CredentialDTO credentialDTO) throws InvalidUserIDException, InvalidPasswordException {
        VerifyCredentialsRequest credentialsRequest
                = convertToVerifyCredentialsRequest(credentialDTO);

        VerifyCredentialsResponse credentialsResponse =
                userDataServiceStub.verifyCredentials(credentialsRequest);

        return validateCredentials(credentialDTO, credentialsResponse);
    }

    private VerifyCredentialsRequest convertToVerifyCredentialsRequest(CredentialDTO credentialDTO) {
        return VerifyCredentialsRequest
                .newBuilder()
                .setUserId(credentialDTO.getUserId())
                .build();
    }

    private String validateCredentials(CredentialDTO credentialDTO, VerifyCredentialsResponse credentialsResponse)
            throws InvalidUserIDException, InvalidPasswordException {
        String password = credentialsResponse.getPassword();
        String name = credentialsResponse.getName();

        if (password == null) {
            throw new InvalidUserIDException(credentialDTO.getUserId());
        }

        if (!credentialDTO.getPassword().equals(password)) {
            throw new InvalidPasswordException(credentialDTO.getUserId());
        }

        return name;
    }
}

