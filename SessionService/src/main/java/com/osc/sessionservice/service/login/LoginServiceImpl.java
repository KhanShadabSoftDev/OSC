package com.osc.sessionservice.service.login;

import com.osc.sessionservice.dto.CredentialDTO;
import com.osc.sessionservice.dto.LoginResponseDto;
import io.grpc.StatusRuntimeException;
import org.springframework.stereotype.Service;


@Service
public class LoginServiceImpl implements LoginService {

//    private final UserDataServiceGrpc.UserDataServiceBlockingStub userDataServiceStub;
//    private final SessionServiceGrpc.SessionServiceBlockingStub sessionServiceStub;
//    private final ManagedChannel userDataChannel;
//    private final ManagedChannel sessionDataChannel;


//    public LoginServiceImpl(UserDataServiceGrpc.UserDataServiceBlockingStub userDataServiceStub,
//                            SessionServiceGrpc.SessionServiceBlockingStub sessionServiceStub,
//                            @Qualifier("managedChannel1") ManagedChannel userDataChannel,
//                            @Qualifier("managedChannel2") ManagedChannel sessionDataChannel) {  // Differentiating with @Qualifier
//        this.userDataServiceStub = userDataServiceStub;
//        this.sessionServiceStub = sessionServiceStub;
//        this.userDataChannel = userDataChannel;
//        this.sessionDataChannel = sessionDataChannel;
//    }


//    @Override
//    public LoginResponseDto createSession(CredentialDTO credentialDTO) {
//
//        // Step 1: Verify user credentials using gRPC call to UserDataService
//        try {
//
//            VerifyCredentialsRequest credentialsRequest = VerifyCredentialsRequest.newBuilder()
//                    .setUserId(credentialDTO.getUserId())
//                    .build();
//
//            VerifyCredentialsResponse credentialsResponse
//                    = userDataServiceStub.verifyCredentials(credentialsRequest);
//
//            String password = credentialsResponse.getPassword();
//            String name = credentialsResponse.getName();
//
//            // Case I: If userID is invalid
//            if (password == null) {
//                throw new InvalidUserIDException(credentialDTO.getUserId());
//            }
//
//            // Case II: If password is invalid
//            if (!credentialDTO.getPassword().equals(password)) {
//                throw new InvalidPasswordException(credentialDTO.getUserId());
//            }
//
//            // Step 2: Verify the session status from with a grpc call to SessionDataService to check from Session-GKT (kafka)
//            SessionStatusRequest sessionStatusRequest = SessionStatusRequest.newBuilder()
//                    .setUserId(credentialDTO.getUserId())
//                    .setDeviceId(credentialDTO.getDeviceName())
//                    .build();
//
//            SessionStatusResponse sessionStatusResponse = sessionServiceStub.checkSessionStatus(sessionStatusRequest);
//
//            // Case III: If session already exists
//            if (sessionStatusResponse.getStatus()) {
//                throw new ActiveSessionException(credentialDTO.getUserId());
//            }
//
//
//            // Step 3: Create a new session ID and save the session details to the database
//            String sessionId = SessionUtility.generateSessionId();
//
//            CreateSessionRequest createSessionRequest = CreateSessionRequest.newBuilder()
//                    .setSessionId(sessionId)
//                    .setDeviceId(credentialDTO.getDeviceName())
//                    .setUserId(credentialDTO.getUserId())
//                    .build();
//
//            CreateSessionResponse createSessionResponse = sessionServiceStub.createSession(createSessionRequest);
//
//            // Return true if the session was created successfully, return session Id and Name Otherwise Null.
//            if (createSessionResponse.getStatus()) {
//                return new LoginResponseDto(sessionId, name);
//            }
//        } catch (StatusRuntimeException ex) {
//            System.out.println(ex.getLocalizedMessage() + "createSession part");
//            return null;
//        }
//        return null;
//    }

    private final UserCredentialService userCredentialService;
    private final SessionStatusService sessionStatusService;
    private final SessionManagementService sessionManagementService;

    public LoginServiceImpl(UserCredentialService userCredentialService, SessionStatusService sessionStatusService,
                        SessionManagementService sessionManagementService) {
        this.userCredentialService = userCredentialService;
        this.sessionStatusService = sessionStatusService;
        this.sessionManagementService = sessionManagementService;
    }

    @Override
    public LoginResponseDto createSession(CredentialDTO credentialDTO) {
        try {
            // Step 1: Verify user credentials
            String name = userCredentialService.verifyUserCredentials(credentialDTO);

            // Step 2: Verify session status
            sessionStatusService.checkSessionStatus(credentialDTO);

            // Step 3: Create and save the session
            return sessionManagementService.createAndSaveSession(credentialDTO, name);

        } catch (StatusRuntimeException ex) {
            System.out.println(ex.getLocalizedMessage() + "createSession part");
            return null;
        }
    }


}



