package com.agricultural.agricultural.mapper;

import com.agricultural.agricultural.domain.entity.ForumReply;
import com.agricultural.agricultural.dto.ForumReplyDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ForumReplyMapper {
    @Mapping(source = "forumPost.id", target = "postId")
    @Mapping(source = "user.id", target = "userId")
    ForumReplyDTO toDto(ForumReply reply);
}


