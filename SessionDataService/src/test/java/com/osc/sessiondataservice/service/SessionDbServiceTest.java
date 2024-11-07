package com.osc.sessiondataservice.service;


import com.osc.sessiondataservice.model.SessionEntity;
import com.osc.sessiondataservice.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class SessionDbServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private SessionDbService sessionDbService;


    private final String sessionId = "789654";
    private final String deviceName = "WEB";
    private final String userId = "user0001";
    private final LocalDateTime now = LocalDateTime.now();


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);


    }

    @Test
    void saveSession_ShouldReturnSavedSession() {
        SessionEntity session = new SessionEntity(sessionId, deviceName, userId, now, null);

        when(sessionRepository.save(any(SessionEntity.class))).thenReturn(session);

        SessionEntity savedSession = sessionDbService.saveSession(session);

        assertNotNull(savedSession);
        assertEquals(sessionId, savedSession.getSessionId());
    }

    @Test
    void updateSession_ShouldUpdateLogoutTime_WhenSessionIsNotLoggedOut() {
        SessionEntity sessionEntity = new SessionEntity(sessionId, deviceName, userId, now, null);

        when(sessionRepository.findById(anyString())).thenReturn(Optional.of(sessionEntity));
        when(sessionRepository.save(any(SessionEntity.class))).thenAnswer(invocation -> {
            SessionEntity updated = invocation.getArgument(0);
            updated.setLogout(now);
            return updated;
        });

        SessionEntity updatedSession = sessionDbService.updateSession(sessionId);

        assertNotNull(updatedSession);
        assertNotNull(updatedSession.getLogout());
    }


    @Test
    void updateSession_ShouldReturnNull_WhenSessionNotFound() {
        when(sessionRepository.findById(anyString())).thenReturn(Optional.empty());

        SessionEntity updatedSession = sessionDbService.updateSession(sessionId);
        assertNull(updatedSession);
    }
}
