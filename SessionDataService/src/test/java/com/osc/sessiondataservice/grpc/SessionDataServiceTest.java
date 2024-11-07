package com.osc.sessiondataservice.grpc;

import com.grpc.session.*;
import com.osc.avro.Session;
import com.osc.avro.SessionKey;
import com.osc.sessiondataservice.model.SessionEntity;
import com.osc.sessiondataservice.service.SessionDbService;
import com.osc.sessiondataservice.service.SessionService;
import com.osc.sessiondataservice.utility.SessionUtility;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class SessionDataServiceTest {

    @Mock
    private SessionService sessionService;

    @Mock
    private SessionDbService sessionDbService;

    @Mock
    private SessionUtility sessionUtility;

    @InjectMocks
    private SessionDataService sessionDataService;

    private final String userId = "user0001";
    private final String deviceId = "WEB";
    private final String sessionId = "789654";

    SessionKey sessionKey=
            SessionKey.newBuilder().setDeviceId(deviceId).setUserId(userId).build();

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckSessionStatus_Success() {
        // Mock request and response
        SessionStatusRequest request = SessionStatusRequest.newBuilder()
                .setUserId(userId)
                .setDeviceId(deviceId)
                .build();
        StreamObserver<SessionStatusResponse> responseObserver = mock(StreamObserver.class);

        // Mock the behavior of sessionService and sessionUtility
        when(sessionUtility.createSessionTopicKey(anyString(), anyString())).thenReturn("user1##device1");
        when(sessionService.getSessionStatus(sessionKey)).thenReturn(true);

        // Call the method
        sessionDataService.checkSessionStatus(request, responseObserver);

        // Verify the response
        verify(responseObserver).onNext(SessionStatusResponse.newBuilder().setStatus(true).build());
        verify(responseObserver).onCompleted();
    }

    @Test
    void testCheckSessionStatus_Failure() {
        // Mock request and response
        SessionStatusRequest request = SessionStatusRequest.newBuilder()
                .setUserId(userId)
                .setDeviceId(deviceId)
                .build();
        StreamObserver<SessionStatusResponse> responseObserver = mock(StreamObserver.class);

        // Mock an exception in sessionService
        when(sessionUtility.createSessionTopicKey(anyString(), anyString())).thenReturn("user1##device1");
        when(sessionService.getSessionStatus(sessionKey)).thenThrow(new StatusRuntimeException(io.grpc.Status.INTERNAL));

        // Call the method
        sessionDataService.checkSessionStatus(request, responseObserver);

        // Verify that error is sent to the response observer
        verify(responseObserver).onError(any(StatusRuntimeException.class));
    }

    @Test
    void testCreateSession_Success() {
        // Mock request and response
        CreateSessionRequest request = CreateSessionRequest.newBuilder()
                .setSessionId(sessionId)
                .setDeviceId(deviceId)
                .setUserId(userId)
                .build();
        StreamObserver<CreateSessionResponse> responseObserver = mock(StreamObserver.class);

        // Mock session creation and DB save
        SessionEntity mockSessionEntity = new SessionEntity("sessionId1", "device1", "user1", LocalDateTime.now(), null);
        when(sessionDbService.saveSession(any(SessionEntity.class))).thenReturn(mockSessionEntity);

        // Call the method
        sessionDataService.createSession(request, responseObserver);

        // Verify the response
        verify(sessionService).produceSession(anyString(), anyString(), any(Session.class));
        verify(responseObserver).onNext(CreateSessionResponse.newBuilder().setStatus(true).build());
        verify(responseObserver).onCompleted();
    }

    @Test
    void testCreateSession_Failure() {
        // Mock request and response
        CreateSessionRequest request = CreateSessionRequest.newBuilder()
                .setSessionId(sessionId)
                .setDeviceId(deviceId)
                .setUserId(userId)
                .build();
        StreamObserver<CreateSessionResponse> responseObserver = mock(StreamObserver.class);

        // Mock a DB save failure (return null)
        when(sessionDbService.saveSession(any(SessionEntity.class))).thenReturn(null);

        // Call the method
        sessionDataService.createSession(request, responseObserver);

        // Verify the response
        verify(sessionService).produceSession(anyString(), anyString(), any(Session.class));
        verify(responseObserver).onNext(CreateSessionResponse.newBuilder().setStatus(false).build());
        verify(responseObserver).onCompleted();
    }

    @Test
    void testEndSession_Success() {
        // Mock request and response
        EndSessionRequest request = EndSessionRequest.newBuilder()
                .setSessionId(sessionId)
                .build();
        StreamObserver<EndSessionResponse> responseObserver = mock(StreamObserver.class);

        // Mock session update
        SessionEntity mockSessionEntity = new SessionEntity("sessionId1", "device1", "user1", LocalDateTime.now(), null);
        when(sessionDbService.updateSession(anyString())).thenReturn(mockSessionEntity);

        // Call the method
        sessionDataService.endSession(request, responseObserver);

        // Verify the response
        verify(sessionService).produceSession(anyString(), anyString(), any(Session.class));
        verify(responseObserver).onNext(EndSessionResponse.newBuilder().setStatus(true).build());
        verify(responseObserver).onCompleted();
    }

    @Test
    void testEndSession_Failure() {
        // Mock request and response
        EndSessionRequest request = EndSessionRequest.newBuilder()
                .setSessionId(sessionId)
                .build();
        StreamObserver<EndSessionResponse> responseObserver = mock(StreamObserver.class);

        // Mock session update failure (return null)
        when(sessionDbService.updateSession(anyString())).thenReturn(null);

        // Call the method
        sessionDataService.endSession(request, responseObserver);

        // Verify the response
        verify(responseObserver).onNext(EndSessionResponse.newBuilder().setStatus(false).build());
        verify(responseObserver).onCompleted();
    }
}
