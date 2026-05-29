package com.sneakpeak.streetpeak.post;

public enum Platform {
    MYNTRA("Myntra"),
    AJIO("Ajio"),
    FLIPKART("Flipkart"),
    NIKE("Nike"),
    OTHER("Other");

    private final String label;

    Platform(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
