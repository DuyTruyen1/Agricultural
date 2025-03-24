package com.agricultural.agricultural.service.impl;

import com.agricultural.agricultural.domain.entity.ForumPost;
import com.agricultural.agricultural.domain.entity.ForumReply;
import com.agricultural.agricultural.domain.entity.User;
import com.agricultural.agricultural.dto.ForumReplyDTO;
import com.agricultural.agricultural.mapper.ForumReplyMapper;
import com.agricultural.agricultural.repository.ForumPostRepository;
import com.agricultural.agricultural.repository.IForumReplyRepository;
import com.agricultural.agricultural.repository.impl.UserRepository;
import com.agricultural.agricultural.service.IForumReplyService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ForumReplyServiceImpl implements IForumReplyService {

    @Autowired
    private IForumReplyRepository forumReplyRepository;

    @Autowired
    private ForumPostRepository forumPostRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ForumReplyMapper forumReplyMapper;

    @Transactional
    public ForumReplyDTO createReply(Integer postId, String content) {
        // 🔹 Lấy thông tin người dùng từ SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Người dùng chưa đăng nhập hoặc chưa xác thực.");
        }

        // 🔹 Lấy username từ Authentication
        String username = authentication.getName();
        System.out.println("Người dùng đang gọi API: " + username);

        // 🔹 Tìm người dùng từ database
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại trong hệ thống."));

        // 🔹 Kiểm tra role của người dùng
        if (user.getRole() == null ||
                (!user.getRole().getRoleName().equalsIgnoreCase("USER") &&
                        !user.getRole().getRoleName().equalsIgnoreCase("ADMIN"))) {
            throw new RuntimeException("Người dùng không có quyền bình luận.");
        }

        // 🔹 Kiểm tra bài viết có tồn tại không
        ForumPost forumPost = forumPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Bài viết không tồn tại."));

        // 🔹 Kiểm tra user có null không
        if (user == null) {
            throw new RuntimeException("User không tồn tại hoặc không thể lấy thông tin!");
        }

// 🔹 Kiểm tra forumPost có null không
        if (forumPost == null) {
            throw new RuntimeException("Bài viết không tồn tại!");
        }

// 🔹 Tạo ForumReply
        ForumReply reply = new ForumReply();
        reply.setForumPost(forumPost);
        reply.setUser(user);
        reply.setContent(content);

// 🔹 In ra log để debug
        System.out.println("ForumReply chuẩn bị lưu:");
        System.out.println("Post ID: " + forumPost.getId());
        System.out.println("User ID: " + user.getId());
        System.out.println("Content: " + content);

// 🔹 Lưu vào database


        // 🔹 Lưu vào database
        ForumReply savedReply = forumReplyRepository.save(reply);
        return forumReplyMapper.toDto(savedReply);
    }





    public List<ForumReplyDTO> getRepliesByPostId(Integer postId) {
        List<ForumReply> replies = forumReplyRepository.findByForumPostId(postId);
        return replies.stream().map(forumReplyMapper::toDto).collect(Collectors.toList());
    }
}
