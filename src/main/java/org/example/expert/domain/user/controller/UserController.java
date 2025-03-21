package org.example.expert.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserProfileImageSaveResponse;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PutMapping("/users")
    public void changePassword(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody UserChangePasswordRequest userChangePasswordRequest
    ) {
        userService.changePassword(authUser.getId(), userChangePasswordRequest);
    }

    @PostMapping("/users/profile")
    public ResponseEntity<UserProfileImageSaveResponse> updateProfileImage(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestPart("file") MultipartFile file
    ) {
        return ResponseEntity.ok(userService.updateProfileImage(authUser.getId(), file));
    }

    @DeleteMapping("/users/profile")
    public ResponseEntity<Void> deleteProfileImage(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        userService.deleteProfileImage(authUser.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserResponse>> searchUsers(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String nickName
    ) {
        return ResponseEntity.ok(userService.searchUsers(nickName, page, size));
    }
}
