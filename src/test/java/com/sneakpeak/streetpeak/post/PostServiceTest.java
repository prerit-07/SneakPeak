package com.sneakpeak.streetpeak.post;

import com.sneakpeak.streetpeak.comment.CommentRepository;
import com.sneakpeak.streetpeak.user.User;
import com.sneakpeak.streetpeak.vote.PostVoteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PostServiceTest {

    private final PostRepository posts = mock(PostRepository.class);
    private final CommentRepository comments = mock(CommentRepository.class);
    private final PostVoteRepository votes = mock(PostVoteRepository.class);
    private final SneakerVerificationService verifier = mock(SneakerVerificationService.class);
    private final PostService service = new PostService(posts, comments, votes, verifier);

    @Test
    void salePostRequiresAllSaleFields() {
        PostForm form = validForm();
        form.setSale(true);
        BindingResult result = new BeanPropertyBindingResult(form, "postForm");

        service.validateSaleFields(form, result);

        assertThat(result.getFieldError("platform")).isNotNull();
        assertThat(result.getFieldError("eventName")).isNotNull();
        assertThat(result.getFieldError("offeredPrice")).isNotNull();
        assertThat(result.getFieldError("offerDetails")).isNotNull();
    }

    @Test
    void offeredPriceCannotExceedRegularPrice() {
        PostForm form = validSaleForm();
        form.setPrice(new BigDecimal("5000"));
        form.setOfferedPrice(new BigDecimal("6000"));
        BindingResult result = new BeanPropertyBindingResult(form, "postForm");

        service.validateSaleFields(form, result);

        assertThat(result.getFieldError("offeredPrice")).isNotNull();
    }

    @Test
    void rejectedVerificationDoesNotSavePost() {
        PostForm form = validForm();
        BindingResult result = new BeanPropertyBindingResult(form, "postForm");
        when(verifier.verify(form)).thenReturn(VerificationResult.rejected("Post must be about sneakers."));

        Post saved = service.create(form, result, new User());

        assertThat(saved).isNull();
        assertThat(result.getGlobalError()).isNotNull();
        verify(posts, never()).save(any());
    }

    @Test
    void approvedVerificationSavesPost() {
        PostForm form = validForm();
        BindingResult result = new BeanPropertyBindingResult(form, "postForm");
        when(verifier.verify(form)).thenReturn(VerificationResult.approved("KEYWORD"));
        when(posts.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Post saved = service.create(form, result, new User());

        assertThat(saved).isNotNull();
        assertThat(saved.getVerificationMethod()).isEqualTo("KEYWORD");
        verify(posts).save(any(Post.class));
    }

    private PostForm validForm() {
        PostForm form = new PostForm();
        form.setSneakerName("Nike Dunk");
        form.setPrice(new BigDecimal("8999"));
        form.setDescription("Saw this pair today.");
        return form;
    }

    private PostForm validSaleForm() {
        PostForm form = validForm();
        form.setSale(true);
        form.setPlatform(Platform.NIKE);
        form.setEventName("End of Season Sale");
        form.setOfferedPrice(new BigDecimal("7999"));
        form.setOfferDetails("Coupon applies at checkout.");
        return form;
    }
}
