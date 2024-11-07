package com.osc.sessiondataservice.service;

import com.osc.avro.Session;
import com.osc.avro.SessionKey;
import com.osc.sessiondataservice.kafka.consumer.KafkaRecordService;
import com.osc.sessiondataservice.kafka.producer.RecordProducer;
import com.osc.sessiondataservice.utility.SessionUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class SessionServiceTest {

    @Mock
    private KafkaRecordService kafkaRecordService;

    @Mock
    private RecordProducer producer;

    @Mock
    private SessionUtility sessionUtility;

    @InjectMocks
    private SessionService sessionService;


    String userId = "user0001";
    String deviceName = "WEB";

    SessionKey sessionKey= SessionKey.newBuilder().setDeviceId(deviceName).setUserId(userId).build();


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getSessionStatus_ShouldReturnTrue_WhenSessionIsActive() {
        Session session = Session.newBuilder().setIsActive(true).build();

        when(kafkaRecordService.getSessionBySessionId(sessionKey)).thenReturn(session);

        boolean status = sessionService.getSessionStatus(sessionKey);

        assertTrue(status);
    }

    @Test
    void getSessionStatus_ShouldReturnFalse_WhenSessionIsInactive() {
        Session session = Session.newBuilder().setIsActive(false).build();

        when(kafkaRecordService.getSessionBySessionId(sessionKey)).thenReturn(session);

        boolean status = sessionService.getSessionStatus(sessionKey);

        assertFalse(status);
    }

    @Test
    void getSessionStatus_ShouldReturnFalse_WhenSessionIsNull() {

        when(kafkaRecordService.getSessionBySessionId(sessionKey)).thenReturn(null);

        boolean status = sessionService.getSessionStatus(sessionKey);

        assertFalse(status);
    }

    @Test
    void produceSession_ShouldCallProducer_WhenSuccessful() {

        Session session = Session.newBuilder().setIsActive(true).build();

        sessionService.produceSession(userId, deviceName, session);

        verify(producer).producerSession(sessionKey, session);
    }


}
