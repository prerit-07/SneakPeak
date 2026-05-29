package com.sneakpeak.streetpeak.common;

import com.sneakpeak.streetpeak.security.SneakPeakUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    @ModelAttribute("currentDisplayName")
    public String currentDisplayName(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof SneakPeakUserDetails details) {
            return details.getDisplayName();
        }
        if (principal instanceof OAuth2User oauth2User) {
            return oauth2User.getAttribute("displayName");
        }
        return null;
    }
}
