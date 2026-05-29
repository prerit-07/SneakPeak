package com.sneakpeak.streetpeak.comment;

import com.sneakpeak.streetpeak.post.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = "user")
    List<Comment> findByPostOrderByCreatedAtAsc(Post post);

    long countByPost(Post post);
}
