package com.osc.sessionservice.service.login;

import com.grpc.session.SessionServiceGrpc;
import com.grpc.session.SessionServiceGrpc.SessionServiceBlockingStub;

import com.grpc.session.SessionStatusRequest;
import com.grpc.session.SessionStatusResponse;
import com.osc.sessionservice.dto.CredentialDTO;
import com.osc.sessionservice.exception.ActiveSessionException;
import io.grpc.ManagedChannel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class SessionStatusService {

    private final SessionServiceGrpc.SessionServiceBlockingStub sessionServiceStub;
    private final ManagedChannel sessionDataChannel;

    public SessionStatusService(SessionServiceBlockingStub sessionServiceStub,
                                @Qualifier("managedChannel2") ManagedChannel sessionDataChannel) {
        this.sessionServiceStub = sessionServiceStub;
        this.sessionDataChannel =sessionDataChannel;
    }

    public void checkSessionStatus(CredentialDTO credentialDTO) throws ActiveSessionException {
        SessionStatusRequest sessionStatusRequest = convertToSessionStatusRequest(credentialDTO);
        SessionStatusResponse sessionStatusResponse = sessionServiceStub.checkSessionStatus(sessionStatusRequest);

        if (sessionStatusResponse.getStatus()) {
            throw new ActiveSessionException(credentialDTO.getUserId());
        }
    }

    private SessionStatusRequest convertToSessionStatusRequest(CredentialDTO credentialDTO) {
        return SessionStatusRequest.newBuilder()
                .setUserId(credentialDTO.getUserId())
                .setDeviceId(credentialDTO.getDeviceName())
                .build();
    }
}

