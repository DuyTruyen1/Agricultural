package com.agricultural.agricultural.service.impl;

import com.agricultural.agricultural.domain.entity.ForumPost;
import com.agricultural.agricultural.domain.entity.Share;
import com.agricultural.agricultural.domain.entity.User;
import com.agricultural.agricultural.dto.ShareDTO;
import com.agricultural.agricultural.mapper.ShareMapper;
import com.agricultural.agricultural.repository.ForumPostRepository;
import com.agricultural.agricultural.repository.IShareRepository;
import com.agricultural.agricultural.repository.impl.UserRepository;
import com.agricultural.agricultural.service.IShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShareServiceImpl implements IShareService {

    @Autowired
    private IShareRepository shareRepository;

    @Autowired
    private ForumPostRepository forumPostRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShareMapper shareMapper;

    // Sử dụng SecurityContextHolder để lấy thông tin người dùng
    public ShareDTO sharePost(Integer postId) {
        // Lấy thông tin người dùng đã đăng nhập từ Spring Security
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOptional = userRepository.findByUserName(username);

        // Kiểm tra xem người dùng có tồn tại không
        User user = userOptional.orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Kiểm tra bài viết có tồn tại không
        ForumPost forumPost = forumPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Bài viết không tồn tại"));

        // Tạo đối tượng Share
        Share share = new Share();
        share.setForumPost(forumPost);
        share.setUser(user);

        // Lưu chia sẻ và trả về DTO
        Share savedShare = shareRepository.save(share);
        return shareMapper.toDto(savedShare);
    }

    // API lấy tất cả chia sẻ của bài viết
    public List<ShareDTO> getSharesByPostId(Integer postId) {
        List<Share> shares = shareRepository.findByForumPostId(postId);
        return shares.stream().map(shareMapper::toDto).collect(Collectors.toList());
    }
}

