package com.medibook.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.sender}")
    private String senderEmail;

    public void sendEmail(String toEmail, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(buildHtmlEmail(subject, body), true);

            mailSender.send(message);
            System.out.println("Email sent to: " + toEmail);

        } catch (Exception e) {
            System.err.println("Failed to send email to "
                    + toEmail + " : " + e.getMessage());
        }
    }

    private String buildHtmlEmail(String subject, String body) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif;
                               background-color: #f4f4f4; margin: 0; padding: 0; }
                        .container { max-width: 600px; margin: 30px auto;
                                     background: white; border-radius: 8px;
                                     overflow: hidden;
                                     box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
                        .header { background-color: #2563eb;
                                  padding: 24px; text-align: center; }
                        .header h1 { color: white; margin: 0;
                                     font-size: 24px; }
                        .header p { color: #bfdbfe; margin: 4px 0 0; }
                        .content { padding: 32px; }
                        .content h2 { color: #1e3a5f; }
                        .content p { color: #4b5563; line-height: 1.6; }
                        .footer { background-color: #f9fafb;
                                  padding: 16px; text-align: center; }
                        .footer p { color: #9ca3af; font-size: 12px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>MediBook</h1>
                            <p>Your Healthcare Companion</p>
                        </div>
                        <div class="content">
                            <h2>%s</h2>
                            <p>%s</p>
                        </div>
                        <div class="footer">
                            <p>This is an automated message from MediBook.
                               Please do not reply to this email.</p>
                            <p>&copy; 2026 MediBook. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(subject, body);
    }
}