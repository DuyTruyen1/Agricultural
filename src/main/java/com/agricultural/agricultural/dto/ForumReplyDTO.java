package com.agricultural.agricultural.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class ForumReplyDTO {
    private Integer id;
    private Integer postId;
    private Integer userId;
    private String content;
    private LocalDateTime createdAt;
}

