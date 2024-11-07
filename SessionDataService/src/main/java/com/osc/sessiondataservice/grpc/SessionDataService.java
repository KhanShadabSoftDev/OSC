package com.osc.sessiondataservice.grpc;

import com.grpc.session.*;
import com.osc.avro.SessionKey;
import com.osc.sessiondataservice.utility.SessionUtility;
import com.osc.avro.Session;
import com.osc.sessiondataservice.model.SessionEntity;
import com.osc.sessiondataservice.service.SessionDbService;
import com.osc.sessiondataservice.service.SessionService;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@GrpcService
public class SessionDataService extends SessionServiceGrpc.SessionServiceImplBase {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SessionDbService sessionDbService;

    @Autowired
    private SessionUtility sessionUtility;

    @Override
    public void checkSessionStatus(SessionStatusRequest request, StreamObserver<SessionStatusResponse> responseObserver) {
        try {
            // Construct the key for the session (userId##deviceId)
            SessionKey key =
                    SessionKey.newBuilder()
                            .setUserId(request.getUserId())
                            .setDeviceId(request.getDeviceId())
                            .build();
//            String key = sessionUtility.createSessionTopicKey(request.getUserId(), request.getDeviceId());

            boolean sessionStatus =
                    sessionService.getSessionStatus(key);

            SessionStatusResponse sessionStatusResponse =
                    SessionStatusResponse.newBuilder().setStatus(sessionStatus).build();

            responseObserver.onNext(sessionStatusResponse);
            responseObserver.onCompleted();

        } catch (StatusRuntimeException ex) {
            // Handle any exceptions that might occur during processing
            responseObserver.onError(Status.INTERNAL.withDescription("Failed to check Session Status").withCause(ex).asRuntimeException());
        }
    }

    @Override
    public void createSession(CreateSessionRequest request, StreamObserver<CreateSessionResponse> responseObserver) {

        try {
            String sessionId = request.getSessionId();
            String deviceName = request.getDeviceId();
            String userId = request.getUserId();


            Session session = Session.newBuilder().setIsActive(true).build();

//         Produce to Kafka Session Topic.........
            sessionService.produceSession(userId, deviceName, session);

//            Create an Session Entity Object to save in DB.
            SessionEntity sessionEntity
                    = new SessionEntity(sessionId, deviceName, userId, LocalDateTime.now(), null);

//            Save Session in DataBase.....
            SessionEntity savedSession
                    = this.sessionDbService.saveSession(sessionEntity);

            // Build the response based on whether the session was saved successfully
            CreateSessionResponse createSessionResponse = CreateSessionResponse.newBuilder()
                    .setStatus(savedSession != null)
                    .build();

            responseObserver.onNext(createSessionResponse);
            responseObserver.onCompleted();
        } catch (StatusRuntimeException ex) {
            responseObserver.onError(Status.INTERNAL.withDescription("Failed to create Session").withCause(ex).asRuntimeException());
        }
    }

    @Override
    public void endSession(EndSessionRequest request, StreamObserver<EndSessionResponse> responseObserver) {
        try {
            String sessionId = request.getSessionId();

//      Add Logout time
            SessionEntity sessionEntity =
                    sessionDbService.updateSession(sessionId);

//            Update False in status of Session in GKT of Session-Topic
            if (sessionEntity != null) {
                Session session = Session.newBuilder().setIsActive(false).build();
//         Produce to Kafka Session Topic.........
                sessionService.produceSession(sessionEntity.getUserId(), sessionEntity.getDeviceName(), session);
            }

            EndSessionResponse endSessionResponse
                        = EndSessionResponse.newBuilder()
                        .setStatus(sessionEntity != null)
                        .build();

            responseObserver.onNext(endSessionResponse);
            responseObserver.onCompleted();
        } catch (StatusRuntimeException ex) {
            responseObserver.onError(Status.INTERNAL.withDescription("Failed to End Session").withCause(ex).asRuntimeException());
        }

    }
}

