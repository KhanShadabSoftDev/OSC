package com.osc.mailservice.consumer;

import com.osc.mailservice.config.MailApplicationConfig;
import com.osc.mailservice.service.SendMail;
import com.osc.avro.OtpMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MailConsumerService {

    private final SendMail mailService;

    public MailConsumerService(SendMail mailService) {
        this.mailService = mailService;
    }

    @KafkaListener(topics = MailApplicationConfig.OTP_TOPIC, groupId = MailApplicationConfig.GRP_ID)
        public void consumeOtp(ConsumerRecord<String, OtpMessage> consumerRecord) {
            OtpMessage otpMessage = consumerRecord.value();
            // Proceed only if the consumed OtpMessage is not null
            if (otpMessage != null) {
                String purpose = otpMessage.getPurpose().toString();

                // Handle the REGISTRATION purpose
                if ("REGISTRATION".equalsIgnoreCase(purpose) && otpMessage.getFailedAttempts() == 0) {
                    mailService.sendRegistrationMail(otpMessage);
                }
                // Handle the FORGET_PASSWORD purpose
                else if ("FORGET_PASSWORD".equalsIgnoreCase(purpose) && otpMessage.getFailedAttempts() == 0) {
                    mailService.sendForgotPasswordMail(otpMessage);
                }
            }
        }
    }




