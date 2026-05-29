package com.sneakpeak.streetpeak.post;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = "user")
    List<Post> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = "user")
    Optional<Post> findWithUserById(Long id);
}
