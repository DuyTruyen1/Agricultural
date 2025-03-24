package com.agricultural.agricultural.repository;

import com.agricultural.agricultural.domain.entity.Share;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IShareRepository extends JpaRepository<Share, Integer> {
    List<Share> findByForumPostId(Integer forumPostId);
    List<Share> findByUserId(Integer userId);
}
