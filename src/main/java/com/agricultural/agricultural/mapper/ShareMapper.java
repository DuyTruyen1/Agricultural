package com.agricultural.agricultural.mapper;

import com.agricultural.agricultural.domain.entity.Share;
import com.agricultural.agricultural.dto.ShareDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShareMapper {

    ShareDTO toDto(Share share);

    Share toEntity(ShareDTO shareDTO);
}
