package com.agricultural.agricultural.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class ShareDTO {
    private Integer id;
    private Integer userId;
    private Integer postId;
    private LocalDateTime shareDate;

}
