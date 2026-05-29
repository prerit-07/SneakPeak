package com.sneakpeak.streetpeak.auth;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        if (!model.containsAttribute("signupForm")) {
            model.addAttribute("signupForm", new SignupForm());
        }
        return "signup";
    }

    @PostMapping("/auth/signup")
    public String signup(
            @Valid @ModelAttribute SignupForm signupForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "signup";
        }
        try {
            SignupResult result = authService.signup(signupForm);
            if (result.devVerificationLink() != null) {
                redirectAttributes.addFlashAttribute("verificationLink", result.devVerificationLink());
            }
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("signup.failed", ex.getMessage());
            return "signup";
        }
        redirectAttributes.addFlashAttribute("message", "Signup successful. Check your email to verify your account.");
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/auth/verify-email")
    public String verifyEmail(@RequestParam String token, RedirectAttributes redirectAttributes) {
        if (authService.verifyEmail(token)) {
            redirectAttributes.addFlashAttribute("message", "Email verified. You can log in now.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Verification link is invalid, expired, or already used.");
        }
        return "redirect:/login";
    }
}
