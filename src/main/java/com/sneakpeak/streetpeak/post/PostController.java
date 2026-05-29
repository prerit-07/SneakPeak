package com.sneakpeak.streetpeak.post;

import com.sneakpeak.streetpeak.comment.CommentService;
import com.sneakpeak.streetpeak.security.CurrentUserService;
import com.sneakpeak.streetpeak.user.User;
import com.sneakpeak.streetpeak.vote.VoteService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final VoteService voteService;
    private final CurrentUserService currentUserService;

    public PostController(PostService postService, CommentService commentService, VoteService voteService, CurrentUserService currentUserService) {
        this.postService = postService;
        this.commentService = commentService;
        this.voteService = voteService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/")
    public String feed(Model model) {
        model.addAttribute("posts", postService.feed());
        return "feed";
    }

    @GetMapping("/posts/new")
    public String newPost(Model model) {
        if (!model.containsAttribute("postForm")) {
            model.addAttribute("postForm", new PostForm());
        }
        model.addAttribute("platforms", Platform.values());
        return "post-form";
    }

    @PostMapping("/posts")
    public String createPost(
            @Valid @ModelAttribute PostForm postForm,
            BindingResult bindingResult,
            Authentication authentication,
            Model model) {
        User user = currentUserService.requireUser(authentication);
        Post post = postService.create(postForm, bindingResult, user);
        if (bindingResult.hasErrors()) {
            model.addAttribute("platforms", Platform.values());
            return "post-form";
        }
        return "redirect:/posts/" + post.getId();
    }

    @GetMapping("/posts/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Post post = postService.getPost(id);
        model.addAttribute("post", post);
        model.addAttribute("voteCount", postService.voteCount(post));
        model.addAttribute("commentCount", postService.commentCount(post));
        model.addAttribute("comments", commentService.commentsFor(post));
        return "post-detail";
    }

    @PostMapping("/posts/{id}/comments")
    public String addComment(
            @PathVariable Long id,
            @RequestParam String content,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            commentService.addComment(id, content, currentUserService.requireUser(authentication));
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{id}/vote")
    public String vote(@PathVariable Long id, Authentication authentication, @RequestParam(required = false) String returnTo) {
        voteService.toggle(id, currentUserService.requireUser(authentication));
        if ("detail".equals(returnTo)) {
            return "redirect:/posts/" + id;
        }
        return "redirect:/";
    }
}
