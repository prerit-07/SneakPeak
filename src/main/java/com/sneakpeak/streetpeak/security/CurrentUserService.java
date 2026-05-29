package com.sneakpeak.streetpeak.security;

import com.sneakpeak.streetpeak.user.User;
import com.sneakpeak.streetpeak.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UserRepository users;

    public CurrentUserService(UserRepository users) {
        this.users = users;
    }

    public User requireUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated.");
        }
        Object principal = authentication.getPrincipal();
        Long userId;
        if (principal instanceof SneakPeakUserDetails details) {
            userId = details.getId();
        } else if (principal instanceof OAuth2User oauth2User) {
            userId = ((Number) oauth2User.getAttribute("localUserId")).longValue();
        } else {
            throw new IllegalStateException("Unsupported principal type.");
        }
        return users.findById(userId).orElseThrow(() -> new IllegalStateException("Authenticated user no longer exists."));
    }
}
