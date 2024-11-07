package com.osc.sessiondataservice.repository;

import com.osc.sessiondataservice.model.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<SessionEntity,String> {
}
