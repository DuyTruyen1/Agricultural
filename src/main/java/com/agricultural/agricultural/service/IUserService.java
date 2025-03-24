package com.agricultural.agricultural.service;

import com.agricultural.agricultural.domain.entity.User;
import com.agricultural.agricultural.dto.UserDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IUserService {

    Optional<UserDTO> findByUserName(String name);

    Optional<User> findByEmail(String email);

    Optional<UserDTO> findById(int id);

    Optional<UserDTO> getUserByEmail(String email);

    boolean existsByEmail(String email);

    User createUser(UserDTO userDTO) throws Exception;

    String login(String email, String password) throws Exception;

    UserDTO updateUser(int id, User user);

    void deleteUser(int id);

    List<UserDTO> getAllUsers();
    
    UserDTO updateUserAvatar(int id, MultipartFile file) throws IOException;
}
