package com.medibook.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private MailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("patelhimanshu609@gmail.com");
        message.setTo(toEmail);
        message.setSubject("MediBook — Email Verification OTP");
        message.setText(
            "Hello!\n\n" +
            "Your OTP for MediBook registration is:\n\n" +
            "  " + otp + "\n\n" +
            "This OTP is valid for 10 minutes.\n" +
            "Do not share this OTP with anyone.\n\n" +
            "— MediBook Team"
        );
        mailSender.send(message);
    }
}