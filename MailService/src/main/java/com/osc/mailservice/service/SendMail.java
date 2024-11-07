package com.osc.mailservice.service;

import com.osc.mailservice.template.EmailTemplate;
import com.osc.avro.OtpMessage;
import org.springframework.stereotype.Service;

@Service
public class SendMail {
    private final MailService mailService;

    public SendMail(MailService mailService) {
        this.mailService = mailService;
    }

    public void sendRegistrationMail(OtpMessage otpMessage){
            String subject = EmailTemplate.REGISTRATION_SUBJECT;
            String body = String.format(EmailTemplate.REGISTRATION_BODY_TEMPLATE,otpMessage.getUserId(), otpMessage.getOtp());
            System.out.println("Subject: " + subject + "  OTP: " + body);
            mailService.sendMail(otpMessage.getEmailId().toString(), subject, body);
    }


    public void sendForgotPasswordMail(OtpMessage otpMessage) {
        String subject = EmailTemplate.FORGOT_PASSWORD_SUBJECT;
        String body = String.format(EmailTemplate.FORGOT_PASSWORD_BODY_TEMPLATE, otpMessage.getOtp());
        System.out.println("Subject: " + subject + "  OTP: " + body);
        mailService.sendMail(otpMessage.getEmailId().toString(), subject, body);
    }

}
