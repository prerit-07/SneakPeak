package com.sneakpeak.streetpeak.vote;

import com.sneakpeak.streetpeak.post.Post;
import com.sneakpeak.streetpeak.post.PostRepository;
import com.sneakpeak.streetpeak.user.User;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VoteServiceTest {

    private final PostRepository posts = mock(PostRepository.class);
    private final PostVoteRepository votes = mock(PostVoteRepository.class);
    private final VoteService service = new VoteService(posts, votes);

    @Test
    void firstVoteCreatesVote() {
        Post post = new Post();
        User user = new User();
        when(posts.findById(1L)).thenReturn(Optional.of(post));
        when(votes.findByPostAndUser(post, user)).thenReturn(Optional.empty());

        service.toggle(1L, user);

        verify(votes).save(any(PostVote.class));
    }

    @Test
    void secondVoteRemovesVote() {
        Post post = new Post();
        User user = new User();
        PostVote vote = new PostVote();
        when(posts.findById(1L)).thenReturn(Optional.of(post));
        when(votes.findByPostAndUser(post, user)).thenReturn(Optional.of(vote));

        service.toggle(1L, user);

        verify(votes).delete(vote);
    }
}
