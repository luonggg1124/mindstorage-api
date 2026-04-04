package com.server.services.others.email;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.server.exceptions.InternalServerException;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImplement implements EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.name}")
    private String mailName;

    @Value("${spring.mail.username}")
    private String mailUsername;

    private static final String OTP_EMAIL_HTML_TEMPLATE = """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
        </head>
        <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f5;">
            <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background-color: #f4f4f5; padding: 40px 20px;">
                <tr>
                    <td align="center">
                        <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="max-width: 480px; background-color: #ffffff; border-radius: 16px; box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08); overflow: hidden;">
                            <tr>
                                <td style="background-color: #6366f1; padding: 32px 40px; text-align: center;">
                                    <h1 style="margin: 0; color: #ffffff; font-size: 24px; font-weight: 600; letter-spacing: -0.5px;">Verification Code</h1>
                                    <p style="margin: 8px 0 0 0; color: #e0e7ff; font-size: 14px;">Confirm your identity</p>
                                </td>
                            </tr>
                            <tr>
                                <td style="padding: 40px 40px 32px;">
                                    <p style="margin: 0 0 24px 0; color: #64748b; font-size: 15px; line-height: 1.6;">Use the code below to complete your verification. This code will expire in <strong style="color: #334155;">10 minutes</strong>.</p>
                                    <table role="presentation" width="100%%" cellspacing="0" cellpadding="0">
                                        <tr>
                                            <td align="center" style="padding: 20px 0;">
                                                <span style="display: inline-block; background-color: #6366f1; color: #ffffff; font-size: 32px; font-weight: 700; letter-spacing: 8px; padding: 20px 32px; border-radius: 12px;">%s</span>
                                            </td>
                                        </tr>
                                    </table>
                                    <p style="margin: 24px 0 0 0; padding: 16px; background-color: #f8fafc; border-radius: 8px; border-left: 4px solid #e2e8f0; color: #64748b; font-size: 13px; line-height: 1.5;">
                                        <strong style="color: #475569;">Security tip:</strong> Never share this code with anyone. We will never ask for it via phone or email.
                                    </p>
                                </td>
                            </tr>
                            <tr>
                                <td style="padding: 24px 40px; background-color: #f8fafc; border-top: 1px solid #e2e8f0;">
                                    <p style="margin: 0; color: #94a3b8; font-size: 12px; text-align: center;">If you didn't request this code, you can safely ignore this email.</p>
                                    <p style="margin: 8px 0 0 0; color: #94a3b8; font-size: 12px; text-align: center;">© %s · All rights reserved.</p>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </body>
        </html>
        """;

    @Override
    @Async("mailExecutor")
    public void sendOtp(String email, String otp){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setTo(email);
            helper.setSubject("Your Verification Code");
            String htmlBody = String.format(OTP_EMAIL_HTML_TEMPLATE, otp, mailName);
            helper.setText(htmlBody, true);
            helper.setFrom(new InternetAddress(mailUsername, mailName, StandardCharsets.UTF_8.name()));
            mailSender.send(message);
            log.info("Sending verification code to {}: {}", email, otp);
        } catch (Exception e) {
            log.error("Failed to send verification code to {}: {}", email, e.getMessage(), e);
            throw new InternalServerException("Failed to send verification code: " + e.getMessage());
        }
    }
}
