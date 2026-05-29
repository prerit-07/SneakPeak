package com.sneakpeak.streetpeak.comment;

import com.sneakpeak.streetpeak.post.Post;
import com.sneakpeak.streetpeak.post.PostRepository;
import com.sneakpeak.streetpeak.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository comments;
    private final PostRepository posts;

    public CommentService(CommentRepository comments, PostRepository posts) {
        this.comments = comments;
        this.posts = posts;
    }

    @Transactional(readOnly = true)
    public List<Comment> commentsFor(Post post) {
        return comments.findByPostOrderByCreatedAtAsc(post);
    }

    @Transactional
    public void addComment(Long postId, String content, User user) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Comment cannot be empty.");
        }
        Post post = posts.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found."));
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(content.trim());
        comments.save(comment);
    }
}
