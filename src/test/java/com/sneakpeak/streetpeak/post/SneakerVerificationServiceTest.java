package com.sneakpeak.streetpeak.post;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SneakerVerificationServiceTest {

    @Test
    void keywordPostPassesWithoutAi() {
        ObjectProvider provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(null);
        SneakerVerificationService service = new SneakerVerificationService(provider);

        PostForm form = new PostForm();
        form.setSneakerName("Nike Air Max");
        form.setPrice(new BigDecimal("9999"));
        form.setDescription("Comfortable daily sneaker.");

        VerificationResult result = service.verify(form);

        assertThat(result.approved()).isTrue();
        assertThat(result.method()).isEqualTo("KEYWORD");
    }

    @Test
    void uncertainPostRejectsWhenAiUnavailable() {
        ObjectProvider provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(null);
        SneakerVerificationService service = new SneakerVerificationService(provider);

        PostForm form = new PostForm();
        form.setSneakerName("Random item");
        form.setPrice(new BigDecimal("99"));
        form.setDescription("Not related.");

        VerificationResult result = service.verify(form);

        assertThat(result.approved()).isFalse();
    }
}
