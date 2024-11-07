package com.osc.sessiondataservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Session_Table")
public class SessionEntity {

    @Id
    private String sessionId;

    private String deviceName;

    private String userId;

    private LocalDateTime login;

    private LocalDateTime logout;




}
