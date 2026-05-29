package com.sneakpeak.streetpeak.security;

import com.sneakpeak.streetpeak.auth.AuthService;
import com.sneakpeak.streetpeak.user.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoogleOAuth2UserService extends DefaultOAuth2UserService {

    private final AuthService authService;

    public GoogleOAuth2UserService(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauthUser = super.loadUser(userRequest);
        String email = oauthUser.getAttribute("email");
        String sub = oauthUser.getAttribute("sub");
        String name = oauthUser.getAttribute("name");
        if (email == null || sub == null) {
            throw new OAuth2AuthenticationException("Google account did not provide email identity.");
        }

        User user = authService.findOrCreateGoogleUser(email, sub, name);
        Map<String, Object> attributes = new HashMap<>(oauthUser.getAttributes());
        attributes.put("localUserId", user.getId());
        attributes.put("displayName", user.getUsername());
        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "sub");
    }
}
