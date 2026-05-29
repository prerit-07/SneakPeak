package com.sneakpeak.streetpeak.auth;

import com.sneakpeak.streetpeak.common.SlugUtil;
import com.sneakpeak.streetpeak.user.AuthProvider;
import com.sneakpeak.streetpeak.user.EmailVerificationToken;
import com.sneakpeak.streetpeak.user.EmailVerificationTokenRepository;
import com.sneakpeak.streetpeak.user.User;
import com.sneakpeak.streetpeak.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository users;
    private final EmailVerificationTokenRepository tokens;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationMailService mailService;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Duration tokenExpiry;

    public AuthService(
            UserRepository users,
            EmailVerificationTokenRepository tokens,
            PasswordEncoder passwordEncoder,
            EmailVerificationMailService mailService,
            @Value("${app.email-verification.expiry-hours}") long expiryHours) {
        this.users = users;
        this.tokens = tokens;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.tokenExpiry = Duration.ofHours(expiryHours);
    }

    @Transactional
    public SignupResult signup(SignupForm form) {
        String email = form.getEmail().trim().toLowerCase(Locale.ROOT);
        String username = form.getUsername().trim();
        if (users.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email is already registered.");
        }
        if (users.existsByUsernameIgnoreCase(username)) {
            throw new IllegalArgumentException("Username is already taken.");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        user.setProvider(AuthProvider.LOCAL);
        user.setEmailVerified(false);
        users.save(user);

        String rawToken = generateToken();
        EmailVerificationToken token = new EmailVerificationToken();
        token.setUser(user);
        token.setTokenHash(TokenHasher.sha256(rawToken));
        token.setExpiresAt(Instant.now().plus(tokenExpiry));
        tokens.save(token);
        String devVerificationLink = mailService.sendVerification(user, rawToken);
        return new SignupResult(devVerificationLink);
    }

    @Transactional
    public boolean verifyEmail(String rawToken) {
        return tokens.findByTokenHash(TokenHasher.sha256(rawToken))
                .filter(token -> token.getUsedAt() == null)
                .filter(token -> token.getExpiresAt().isAfter(Instant.now()))
                .map(token -> {
                    token.setUsedAt(Instant.now());
                    token.getUser().setEmailVerified(true);
                    return true;
                })
                .orElse(false);
    }

    @Transactional
    public User findOrCreateGoogleUser(String email, String providerId, String name) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        return users.findByEmailIgnoreCase(normalizedEmail)
                .map(existing -> {
                    existing.setProvider(AuthProvider.GOOGLE);
                    existing.setProviderId(providerId);
                    existing.setEmailVerified(true);
                    return existing;
                })
                .orElseGet(() -> {
                    User user = new User();
                    user.setEmail(normalizedEmail);
                    user.setUsername(uniqueGoogleUsername(normalizedEmail, name));
                    user.setProvider(AuthProvider.GOOGLE);
                    user.setProviderId(providerId);
                    user.setEmailVerified(true);
                    return users.save(user);
                });
    }

    private String uniqueGoogleUsername(String email, String name) {
        String base = SlugUtil.usernameFromEmail(name != null ? name : email);
        String candidate = base;
        while (users.existsByUsernameIgnoreCase(candidate)) {
            candidate = base + SlugUtil.shortSuffix();
        }
        return candidate;
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
