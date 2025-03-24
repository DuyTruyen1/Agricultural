package com.agricultural.agricultural.service.impl;

import com.agricultural.agricultural.domain.entity.Role;
import com.agricultural.agricultural.domain.entity.User;
import com.agricultural.agricultural.components.JwtTokenUtil;
import com.agricultural.agricultural.dto.UserDTO;
import com.agricultural.agricultural.exception.DataNotFoundException;
import com.agricultural.agricultural.exception.PermissionDenyException;
import com.agricultural.agricultural.mapper.UserMapper;
import com.agricultural.agricultural.repository.IRoleRepository;
import com.agricultural.agricultural.repository.impl.UserRepository;
import com.agricultural.agricultural.service.IUserService;
import com.agricultural.agricultural.service.CloudinaryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService implements IUserService {

    AuthenticationManager authenticationManager;
    IRoleRepository roleRepository;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    JwtTokenUtil jwtTokenUtil;
    UserMapper userMapper;
    private final CloudinaryService cloudinaryService;

    @Autowired
    public UserService(UserMapper userMapper, AuthenticationManager authenticationManager,
                       IRoleRepository roleRepository, UserRepository userRepository,
                       PasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil,
                       CloudinaryService cloudinaryService) {
        this.userMapper = userMapper;
        this.authenticationManager = authenticationManager;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public Optional<UserDTO> findById(int id) {
        return userRepository.findById(id)
                .map(userMapper::toDTO); // ✅ Dùng UserMapper để chuyển đổi
    }


    @Override
    public Optional<UserDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email).map(userMapper::toDTO);
    }


    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User createUser(UserDTO userDTO) throws Exception {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new DataIntegrityViolationException("Email already exists");
        }

        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new DataNotFoundException("Role not found"));

        if (role.getName().equalsIgnoreCase("ADMIN")) {
            throw new PermissionDenyException("You cannot register an admin account");
        }

        // Mặc định ảnh đại diện
        if (userDTO.getImageUrl() == null || userDTO.getImageUrl().isEmpty()) {
            userDTO.setImageUrl("default_profile.png");
        }

        // Sử dụng Mapper để chuyển đổi DTO -> Entity
        User newUser = userMapper.toEntity(userDTO);
        newUser.setPassword(passwordEncoder.encode(userDTO.getPassword())); // Mã hóa mật khẩu
        newUser.setRole(role); // Gán Role

        return userRepository.save(newUser);
    }

    @Override
    public String login(String email, String password) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException("Invalid email / password");
        }

        User existingUser = optionalUser.get();
        if (!passwordEncoder.matches(password, existingUser.getPassword())) {
            throw new BadCredentialsException("Wrong email or password");
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                email, password, existingUser.getAuthorities()
        );

        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtil.generateToken(existingUser);
    }

    @Override
    public UserDTO updateUser(int id, User updatedUser) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    // Cập nhật thông tin
                    if (updatedUser.getUsername() != null) {
                        existingUser.setUserName(updatedUser.getUsername());
                    }
                    if (updatedUser.getEmail() != null) {
                        existingUser.setEmail(updatedUser.getEmail());
                    }
                    if (updatedUser.getPhone() != null) {
                        existingUser.setPhone(updatedUser.getPhone());
                    }
                    // Không cập nhật imageUrl ở đây, sử dụng phương thức updateUserAvatar riêng
                    
                    User savedUser = userRepository.save(existingUser);
                    return userMapper.toDTO(savedUser);
                })
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
    }

    @Override
    public Optional<UserDTO> findByUserName(String name) {
        return userRepository.findByUserName(name)
                .map(userMapper::toDTO); // ✅ Dùng Mapper để chuyển đổi Entity → DTO
    }




    @Override
    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO updateUserAvatar(int id, MultipartFile file) throws IOException {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }

        User user = userOptional.get();
        
        // Nếu người dùng đã có avatar (không phải mặc định), xóa ảnh cũ trên Cloudinary
        if (user.getImageUrl() != null && !user.getImageUrl().equals("default_profile.png")) {
            // Lấy public_id từ URL (cần xử lý chuỗi URL để lấy public_id)
            String publicId = extractPublicIdFromUrl(user.getImageUrl());
            if (publicId != null) {
                cloudinaryService.delete(publicId);
            }
        }

        // Tải lên ảnh mới
        Map<?, ?> uploadResult = cloudinaryService.upload(file);
        String imageUrl = (String) uploadResult.get("url");
        user.setImageUrl(imageUrl);
        
        userRepository.save(user);
        return userMapper.toDTO(user);
    }
    
    // Helper method to extract public_id from Cloudinary URL
    private String extractPublicIdFromUrl(String imageUrl) {
        if (imageUrl != null && imageUrl.contains("/user-avatars/")) {
            // Example URL: https://res.cloudinary.com/your-cloud-name/image/upload/v1234567890/user-avatars/abcdef123456
            // We need to extract: user-avatars/abcdef123456
            try {
                String[] parts = imageUrl.split("/upload/");
                if (parts.length > 1) {
                    String afterUpload = parts[1];
                    // Remove version if exists
                    if (afterUpload.startsWith("v")) {
                        afterUpload = afterUpload.substring(afterUpload.indexOf("/") + 1);
                    }
                    return afterUpload.contains(".") ? 
                        afterUpload.substring(0, afterUpload.lastIndexOf(".")) : 
                        afterUpload;
                }
            } catch (Exception e) {
                // Log error but don't fail
                System.err.println("Error extracting public ID: " + e.getMessage());
            }
        }
        return null;
    }
}
