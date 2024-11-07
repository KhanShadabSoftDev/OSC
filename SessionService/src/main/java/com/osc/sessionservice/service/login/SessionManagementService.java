package com.osc.sessionservice.service.login;

import com.grpc.session.CreateSessionRequest;
import com.grpc.session.CreateSessionResponse;
import com.grpc.session.SessionServiceGrpc;
import com.grpc.session.SessionServiceGrpc.SessionServiceBlockingStub;
import com.osc.sessionservice.dto.CredentialDTO;
import com.osc.sessionservice.dto.LoginResponseDto;
import com.osc.sessionservice.utility.SessionUtility;
import io.grpc.ManagedChannel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class SessionManagementService {

    private final SessionServiceGrpc.SessionServiceBlockingStub sessionServiceStub;
    private final ManagedChannel sessionDataChannel;


    public SessionManagementService(SessionServiceBlockingStub sessionServiceStub,
                                    @Qualifier("managedChannel2") ManagedChannel sessionDataChannel) {
        this.sessionServiceStub = sessionServiceStub;
        this.sessionDataChannel = sessionDataChannel;
    }

    public LoginResponseDto createAndSaveSession(CredentialDTO credentialDTO, String name) {
        String sessionId = SessionUtility.generateSessionId();
        CreateSessionRequest createSessionRequest = convertToCreateSessionRequest(credentialDTO, sessionId);
        CreateSessionResponse createSessionResponse = sessionServiceStub.createSession(createSessionRequest);

        if (createSessionResponse.getStatus()) {
            return new LoginResponseDto(sessionId, name);
        }

        return null;
    }

    private CreateSessionRequest convertToCreateSessionRequest(CredentialDTO credentialDTO, String sessionId) {
        return CreateSessionRequest.newBuilder()
                .setSessionId(sessionId)
                .setDeviceId(credentialDTO.getDeviceName())
                .setUserId(credentialDTO.getUserId())
                .build();
    }
}
