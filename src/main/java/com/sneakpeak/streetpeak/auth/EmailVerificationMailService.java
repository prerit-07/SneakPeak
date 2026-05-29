package com.sneakpeak.streetpeak.auth;

import com.sneakpeak.streetpeak.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmailVerificationMailService {

    private static final Logger log = LoggerFactory.getLogger(EmailVerificationMailService.class);

    private final MailSender mailSender;
    private final String mailHost;
    private final String baseUrl;

    public EmailVerificationMailService(
            MailSender mailSender,
            @Value("${spring.mail.host:}") String mailHost,
            @Value("${app.base-url}") String baseUrl) {
        this.mailSender = mailSender;
        this.mailHost = mailHost;
        this.baseUrl = baseUrl;
    }

    public String sendVerification(User user, String rawToken) {
        String link = baseUrl + "/auth/verify-email?token=" + rawToken;
        if (!StringUtils.hasText(mailHost)) {
            log.warn("Email is not configured. Verification link for {}: {}", user.getEmail(), link);
            return link;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Verify your SneakPeak email");
        message.setText("Open this link to verify your SneakPeak account:\n\n" + link);
        try {
            mailSender.send(message);
        } catch (MailException ex) {
            log.warn("Could not send verification email to {}. Link: {}", user.getEmail(), link, ex);
        }
        return null;
    }
}
