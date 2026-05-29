package com.sneakpeak.streetpeak.post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;

@Service
public class SneakerVerificationService {

    private static final Logger log = LoggerFactory.getLogger(SneakerVerificationService.class);
    private static final List<String> SNEAKER_KEYWORDS = List.of(
            "nike", "puma", "adidas", "jordan", "sneaker", "sneakers", "shoe", "shoes",
            "air max", "air force", "dunk", "yeezy", "new balance", "asics", "converse", "vans",
            "reebok", "samba", "gazelle", "ultraboost", "air jordan"
    );

    private final ObjectProvider<ChatClient.Builder> chatClientBuilder;

    public SneakerVerificationService(ObjectProvider<ChatClient.Builder> chatClientBuilder) {
        this.chatClientBuilder = chatClientBuilder;
    }

    public VerificationResult verify(PostForm form) {
        String content = combinedContent(form);
        String normalized = content.toLowerCase(Locale.ROOT);
        if (SNEAKER_KEYWORDS.stream().anyMatch(normalized::contains)) {
            return VerificationResult.approved("KEYWORD");
        }

        ChatClient.Builder builder = chatClientBuilder.getIfAvailable();
        if (builder == null) {
            return VerificationResult.rejected("Post must be about sneakers. AI verification is currently unavailable.");
        }

        try {
            String response = builder.build()
                    .prompt()
                    .user("""
                            Decide if this user submission is about sneakers, shoes, sneaker pricing, sneaker sales, or sneaker deals.
                            Reply with exactly one word: APPROVE or REJECT.

                            Submission:
                            %s
                            """.formatted(content))
                    .call()
                    .content();
            if (response != null && response.trim().equalsIgnoreCase("APPROVE")) {
                return VerificationResult.approved("AI");
            }
            return VerificationResult.rejected("Post must be about sneakers.");
        } catch (RuntimeException ex) {
            log.warn("AI verification failed", ex);
            return VerificationResult.rejected("Could not verify this post right now. Please retry.");
        }
    }

    private String combinedContent(PostForm form) {
        StringBuilder builder = new StringBuilder();
        append(builder, form.getSneakerName());
        append(builder, form.getDescription());
        if (form.getPlatform() != null) {
            append(builder, form.getPlatform().getLabel());
        }
        append(builder, form.getEventName());
        append(builder, form.getOfferDetails());
        return builder.toString();
    }

    private void append(StringBuilder builder, String value) {
        if (StringUtils.hasText(value)) {
            builder.append(value).append('\n');
        }
    }
}
