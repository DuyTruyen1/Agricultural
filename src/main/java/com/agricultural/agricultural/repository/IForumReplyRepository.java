package com.agricultural.agricultural.repository;

import com.agricultural.agricultural.domain.entity.ForumReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IForumReplyRepository extends JpaRepository<ForumReply, Integer> {
    List<ForumReply> findByForumPostId(Integer postId);
    List<ForumReply> findByUserId(Integer userId);
}

