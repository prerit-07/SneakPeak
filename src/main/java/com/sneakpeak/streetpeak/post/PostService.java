package com.sneakpeak.streetpeak.post;

import com.sneakpeak.streetpeak.comment.CommentRepository;
import com.sneakpeak.streetpeak.user.User;
import com.sneakpeak.streetpeak.vote.PostVoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PostService {

    private final PostRepository posts;
    private final CommentRepository comments;
    private final PostVoteRepository votes;
    private final SneakerVerificationService verificationService;

    public PostService(PostRepository posts, CommentRepository comments, PostVoteRepository votes, SneakerVerificationService verificationService) {
        this.posts = posts;
        this.comments = comments;
        this.votes = votes;
        this.verificationService = verificationService;
    }

    @Transactional(readOnly = true)
    public List<PostSummary> feed() {
        return posts.findAllByOrderByCreatedAtDesc().stream()
                .map(post -> new PostSummary(post, votes.countByPost(post), comments.countByPost(post)))
                .toList();
    }

    @Transactional(readOnly = true)
    public Post getPost(Long id) {
        return posts.findWithUserById(id).orElseThrow(() -> new IllegalArgumentException("Post not found."));
    }

    @Transactional(readOnly = true)
    public long voteCount(Post post) {
        return votes.countByPost(post);
    }

    @Transactional(readOnly = true)
    public long commentCount(Post post) {
        return comments.countByPost(post);
    }

    @Transactional
    public Post create(PostForm form, BindingResult bindingResult, User user) {
        validateSaleFields(form, bindingResult);
        if (bindingResult.hasErrors()) {
            return null;
        }

        VerificationResult result = verificationService.verify(form);
        if (!result.approved()) {
            bindingResult.reject("post.verification", result.message());
            return null;
        }

        Post post = new Post();
        post.setUser(user);
        post.setSneakerName(form.getSneakerName().trim());
        post.setPrice(form.getPrice());
        post.setDescription(form.getDescription().trim());
        post.setSale(form.isSale());
        post.setProductLink(form.getProductLink());
        post.setVerificationMethod(result.method());
        if (form.isSale()) {
            post.setPlatform(form.getPlatform());
            post.setEventName(form.getEventName().trim());
            post.setOfferedPrice(form.getOfferedPrice());
            post.setOfferDetails(form.getOfferDetails().trim());
        }
        return posts.save(post);
    }

    public void validateSaleFields(PostForm form, BindingResult bindingResult) {
        if (!form.isSale()) {
            return;
        }
        if (form.getPlatform() == null) {
            bindingResult.rejectValue("platform", "sale.platform.required", "Platform is required for sale posts.");
        }
        if (form.getEventName() == null || form.getEventName().isBlank()) {
            bindingResult.rejectValue("eventName", "sale.eventName.required", "Event name is required for sale posts.");
        }
        if (form.getOfferedPrice() == null) {
            bindingResult.rejectValue("offeredPrice", "sale.offeredPrice.required", "Offered price is required for sale posts.");
        }
        if (form.getOfferDetails() == null || form.getOfferDetails().isBlank()) {
            bindingResult.rejectValue("offerDetails", "sale.offerDetails.required", "Offer details are required for sale posts.");
        }
        if (form.getPrice() != null && form.getOfferedPrice() != null
                && form.getOfferedPrice().compareTo(form.getPrice()) > 0) {
            bindingResult.rejectValue("offeredPrice", "sale.offeredPrice.tooHigh", "Offered price cannot exceed regular price.");
        }
    }

    public record PostSummary(Post post, long voteCount, long commentCount) {

        public BigDecimal displayPrice() {
            return post.isSale() && post.getOfferedPrice() != null ? post.getOfferedPrice() : post.getPrice();
        }

        public String avatarBg() {
            return getAvatarBg();
        }

        public String getAvatarBg() {
            int hash = post.getUser().getUsername().hashCode();
            String[] bgs = { "#ffd6cc", "#d1f2d9", "#dbe6ff", "#fff2cc", "#f3d1f2", "#e2ddd6" };
            return bgs[Math.abs(hash % bgs.length)];
        }

        public String avatarColor() {
            return getAvatarColor();
        }

        public String getAvatarColor() {
            int hash = post.getUser().getUsername().hashCode();
            String[] colors = { "#c82000", "#1a5c2a", "#1b3a99", "#7a5c00", "#991b93", "#3a3a36" };
            return colors[Math.abs(hash % colors.length)];
        }

        public boolean userVoted() {
            return isUserVoted();
        }

        public boolean isUserVoted() {
            return false;
        }
    }
}
