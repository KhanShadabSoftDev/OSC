package com.osc.sessiondataservice.service;

import com.osc.sessiondataservice.exception.SessionAlreadyLoggedOutException;
import com.osc.sessiondataservice.model.SessionEntity;
import com.osc.sessiondataservice.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SessionDbService {

    private final SessionRepository sessionRepository;

    public SessionEntity saveSession(SessionEntity session){
        return this.sessionRepository.save(session);
    }

    public SessionEntity updateSession(String sessionId) {
        Optional<SessionEntity> byId = this.sessionRepository.findById(sessionId);

        // If the session exists, and logout tiem should be null then only  update the logout time and save the updated entity
        if (byId.isPresent()) {
            SessionEntity sessionEntity = byId.get();
            // Set the logout time to the current time
            sessionEntity.setLogout(LocalDateTime.now());
            // Save the updated entity back to the repository
            return this.sessionRepository.save(sessionEntity);
        }
        return null;
    }
}
