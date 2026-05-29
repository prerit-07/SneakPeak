package com.sneakpeak.streetpeak.post;

public record VerificationResult(boolean approved, String method, String message) {

    public static VerificationResult approved(String method) {
        return new VerificationResult(true, method, null);
    }

    public static VerificationResult rejected(String message) {
        return new VerificationResult(false, "REJECTED", message);
    }
}
