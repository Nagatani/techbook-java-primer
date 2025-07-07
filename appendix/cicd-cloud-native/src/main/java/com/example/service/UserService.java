package com.example.service;

import com.example.dto.UserDTO;
import com.example.exception.ResourceNotFoundException;
import com.example.exception.UserAlreadyExistsException;
import com.example.model.User;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * ユーザーサービス
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * すべてのユーザーを取得
     */
    public Page<UserDTO> findAll(Pageable pageable) {
        log.debug("Finding all users with pagination: {}", pageable);
        return userRepository.findAll(pageable)
                .map(this::convertToDTO);
    }
    
    /**
     * IDでユーザーを取得
     */
    @Cacheable(value = "users", key = "#id")
    public UserDTO findById(UUID id) {
        log.debug("Finding user by id: {}", id);
        return userRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
    
    /**
     * ユーザー名でユーザーを取得
     */
    @Cacheable(value = "users", key = "#username")
    public UserDTO findByUsername(String username) {
        log.debug("Finding user by username: {}", username);
        return userRepository.findByUsername(username)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }
    
    /**
     * 新規ユーザーを作成
     */
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public UserDTO create(UserDTO userDTO) {
        log.info("Creating new user: {}", userDTO.getUsername());
        
        // 重複チェック
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists: " + userDTO.getUsername());
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists: " + userDTO.getEmail());
        }
        
        User user = convertToEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user = userRepository.save(user);
        
        log.info("User created successfully: {}", user.getId());
        return convertToDTO(user);
    }
    
    /**
     * ユーザーを更新
     */
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public UserDTO update(UUID id, UserDTO userDTO) {
        log.info("Updating user: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        // 更新処理
        user.setEmail(userDTO.getEmail());
        user.setFullName(userDTO.getFullName());
        user.setActive(userDTO.isActive());
        
        user = userRepository.save(user);
        log.info("User updated successfully: {}", id);
        return convertToDTO(user);
    }
    
    /**
     * ユーザーを削除
     */
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void delete(UUID id) {
        log.info("Deleting user: {}", id);
        
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        
        userRepository.deleteById(id);
        log.info("User deleted successfully: {}", id);
    }
    
    /**
     * EntityからDTOへの変換
     */
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
    
    /**
     * DTOからEntityへの変換
     */
    private User convertToEntity(UserDTO userDTO) {
        return User.builder()
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .fullName(userDTO.getFullName())
                .active(userDTO.isActive())
                .build();
    }
}