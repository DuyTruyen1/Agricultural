package com.agricultural.agricultural.dto;

import com.agricultural.agricultural.domain.entity.User;
import lombok.*;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Getter

public class UserDTO {
    private int id;
    private String userName;
    private String password;
    private String email;
    private String phone;
    private String imageUrl;
    private String roleName; // Thay vì trả về cả object Role, chỉ lấy role name


    public UserDTO(User user) {
        this.id = user.getId();
        this.userName = user.getUsername();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.imageUrl = user.getImageUrl();
        this.roleName = user.getRole() != null ? user.getRole().getName() : "UNKNOWN"; // Xử lý role null
    }


}

