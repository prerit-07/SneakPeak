package com.sneakpeak.streetpeak.vote;

import com.sneakpeak.streetpeak.post.Post;
import com.sneakpeak.streetpeak.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostVoteRepository extends JpaRepository<PostVote, Long> {

    Optional<PostVote> findByPostAndUser(Post post, User user);

    long countByPost(Post post);
}
