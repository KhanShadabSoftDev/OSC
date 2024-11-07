package com.osc.sessionservice.service.logout;

import com.grpc.session.EndSessionRequest;
import com.grpc.session.EndSessionResponse;
import com.grpc.session.SessionServiceGrpc;
import com.osc.sessionservice.dto.LogoutDTO;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


@Service
public class LogoutServiceImpl implements LogoutService {

    private final SessionServiceGrpc.SessionServiceBlockingStub sessionServiceStub;

    private final ManagedChannel sessionDataChannel;

    public LogoutServiceImpl(SessionServiceGrpc.SessionServiceBlockingStub sessionServiceStub
            ,@Qualifier("managedChannel2") ManagedChannel sessionDataChannel) {
        this.sessionServiceStub = sessionServiceStub;
        this.sessionDataChannel = sessionDataChannel;
    }

    @Override
    public boolean LogoutSession(LogoutDTO logoutDTO) {

//        Make a request to session data service and return boolean value....
        try {
            EndSessionRequest endSessionRequest
                    = convertToEndSessionRequest(logoutDTO);

            EndSessionResponse endSessionResponse
                    = sessionServiceStub.endSession(endSessionRequest);

            return endSessionResponse.getStatus();
        } catch (StatusRuntimeException ex) {
            System.out.println(ex.getLocalizedMessage() + "createSession part");
            return false;
        }
    }


    private EndSessionRequest convertToEndSessionRequest(LogoutDTO logoutDTO) {
        return EndSessionRequest.newBuilder()
                .setSessionId(logoutDTO.getSessionId())
                .setUserId(logoutDTO.getUserId())
                .build();
    }

}
