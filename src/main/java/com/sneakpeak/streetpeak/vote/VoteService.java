package com.sneakpeak.streetpeak.vote;

import com.sneakpeak.streetpeak.post.Post;
import com.sneakpeak.streetpeak.post.PostRepository;
import com.sneakpeak.streetpeak.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VoteService {

    private final PostRepository posts;
    private final PostVoteRepository votes;

    public VoteService(PostRepository posts, PostVoteRepository votes) {
        this.posts = posts;
        this.votes = votes;
    }

    @Transactional
    public void toggle(Long postId, User user) {
        Post post = posts.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found."));
        votes.findByPostAndUser(post, user)
                .ifPresentOrElse(votes::delete, () -> {
                    PostVote vote = new PostVote();
                    vote.setPost(post);
                    vote.setUser(user);
                    votes.save(vote);
                });
    }
}
