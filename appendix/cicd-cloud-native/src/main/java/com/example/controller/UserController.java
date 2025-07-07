package com.example.controller;

import com.example.dto.UserDTO;
import com.example.service.UserService;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * ユーザーRESTコントローラー
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Timed("user.controller")
public class UserController {
    
    private final UserService userService;
    
    /**
     * ユーザー一覧を取得
     */
    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("GET /api/v1/users - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<UserDTO> users = userService.findAll(pageable);
        return ResponseEntity.ok(users);
    }
    
    /**
     * IDでユーザーを取得
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        log.info("GET /api/v1/users/{}", id);
        UserDTO user = userService.findById(id);
        return ResponseEntity.ok(user);
    }
    
    /**
     * ユーザー名でユーザーを取得
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        log.info("GET /api/v1/users/username/{}", username);
        UserDTO user = userService.findByUsername(username);
        return ResponseEntity.ok(user);
    }
    
    /**
     * 新規ユーザーを作成
     */
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        log.info("POST /api/v1/users - username: {}", userDTO.getUsername());
        UserDTO createdUser = userService.create(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    
    /**
     * ユーザーを更新
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable UUID id, @Valid @RequestBody UserDTO userDTO) {
        log.info("PUT /api/v1/users/{}", id);
        UserDTO updatedUser = userService.update(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }
    
    /**
     * ユーザーを削除
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        log.info("DELETE /api/v1/users/{}", id);
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}