package com.sneakpeak.streetpeak.auth;

import com.sneakpeak.streetpeak.user.EmailVerificationToken;
import com.sneakpeak.streetpeak.user.EmailVerificationTokenRepository;
import com.sneakpeak.streetpeak.user.User;
import com.sneakpeak.streetpeak.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private final UserRepository users = mock(UserRepository.class);
    private final EmailVerificationTokenRepository tokens = mock(EmailVerificationTokenRepository.class);
    private final EmailVerificationMailService mail = mock(EmailVerificationMailService.class);
    private final AuthService service = new AuthService(users, tokens, new BCryptPasswordEncoder(), mail, 24);

    @Test
    void signupCreatesUnverifiedUserAndToken() {
        SignupForm form = new SignupForm();
        form.setUsername("peakfinder");
        form.setEmail("PEAK@example.com");
        form.setPassword("password123");
        when(users.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mail.sendVerification(any(User.class), anyString())).thenReturn("http://localhost/verify");

        SignupResult result = service.signup(form);

        verify(users).save(any(User.class));
        verify(tokens).save(any(EmailVerificationToken.class));
        verify(mail).sendVerification(any(User.class), any(String.class));
        assertThat(result.devVerificationLink()).isEqualTo("http://localhost/verify");
    }

    @Test
    void signupRejectsDuplicateEmail() {
        SignupForm form = new SignupForm();
        form.setUsername("peakfinder");
        form.setEmail("peak@example.com");
        form.setPassword("password123");
        when(users.existsByEmailIgnoreCase("peak@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.signup(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email");

        verify(tokens, never()).save(any());
    }

    @Test
    void verifyEmailMarksTokenUsedAndUserVerified() {
        User user = new User();
        user.setEmail("peak@example.com");
        EmailVerificationToken token = new EmailVerificationToken();
        token.setUser(user);
        token.setTokenHash(TokenHasher.sha256("raw-token"));
        token.setExpiresAt(java.time.Instant.now().plusSeconds(60));
        when(tokens.findByTokenHash(TokenHasher.sha256("raw-token"))).thenReturn(Optional.of(token));

        boolean verified = service.verifyEmail("raw-token");

        assertThat(verified).isTrue();
        assertThat(user.isEmailVerified()).isTrue();
        assertThat(token.getUsedAt()).isNotNull();
    }
}
