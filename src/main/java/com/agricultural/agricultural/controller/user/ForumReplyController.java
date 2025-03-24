package com.agricultural.agricultural.controller.user;

import com.agricultural.agricultural.dto.ForumReplyDTO;
import com.agricultural.agricultural.dto.ShareDTO;
import com.agricultural.agricultural.service.impl.ForumReplyServiceImpl;
import com.agricultural.agricultural.service.impl.ShareServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/forumReply")
@RequiredArgsConstructor
public class ForumReplyController {
    @Autowired
    private ForumReplyServiceImpl forumReplyService;

    private ShareServiceImpl shareService;

    @PostMapping("/{postId}/reply")
    public ResponseEntity<ForumReplyDTO> replyToPost(@PathVariable Integer postId,
                                                     @RequestBody ForumReplyDTO request) {
        String content = request.getContent();
        ForumReplyDTO forumReplyDTO = forumReplyService.createReply(postId, content);
        return ResponseEntity.ok(forumReplyDTO);
    }


    @PostMapping("/{postId}/share")
    public ResponseEntity<ShareDTO> sharePost(@PathVariable Integer postId) {
        ShareDTO shareDTO = shareService.sharePost(postId);
        return ResponseEntity.ok(shareDTO);
    }

    @GetMapping("/{postId}/replies")
    public ResponseEntity<List<ForumReplyDTO>> getReplies(@PathVariable Integer postId) {
        List<ForumReplyDTO> replies = forumReplyService.getRepliesByPostId(postId);
        return ResponseEntity.ok(replies);
    }

    @GetMapping("/{postId}/shares")
    public ResponseEntity<List<ShareDTO>> getShares(@PathVariable Integer postId) {
        List<ShareDTO> shares = shareService.getSharesByPostId(postId);
        return ResponseEntity.ok(shares);
    }
}
