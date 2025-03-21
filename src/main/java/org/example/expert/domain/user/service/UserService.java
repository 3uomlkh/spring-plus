package org.example.expert.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.aop.annotation.PerformanceCheck;
import org.example.expert.client.s3.S3Service;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserProfileImageSaveResponse;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final S3Service s3Service;

    public UserResponse getUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));
        return UserResponse.of(user);
    }

    @Transactional
    public void changePassword(long userId, UserChangePasswordRequest userChangePasswordRequest) {
        validateNewPassword(userChangePasswordRequest);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        if (passwordEncoder.matches(userChangePasswordRequest.getNewPassword(), user.getPassword())) {
            throw new InvalidRequestException("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
        }

        if (!passwordEncoder.matches(userChangePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new InvalidRequestException("잘못된 비밀번호입니다.");
        }

        user.changePassword(passwordEncoder.encode(userChangePasswordRequest.getNewPassword()));
    }

    private static void validateNewPassword(UserChangePasswordRequest userChangePasswordRequest) {
        if (userChangePasswordRequest.getNewPassword().length() < 8 ||
                !userChangePasswordRequest.getNewPassword().matches(".*\\d.*") ||
                !userChangePasswordRequest.getNewPassword().matches(".*[A-Z].*")) {
            throw new InvalidRequestException("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.");
        }
    }

    @Transactional
    public UserProfileImageSaveResponse updateProfileImage(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        // S3로 파일을 보냄
        String url;
        try {
            url = s3Service.uploadFile(file, "profile");
        } catch (IOException e) {
            throw new InvalidRequestException("프로필 이미지 업로드 실패");
        }
        user.updateProfileImageUrl(url);

        // S3로 올라간 파일 URL을 응답에 넣음
        return new UserProfileImageSaveResponse(url);
    }

    @Transactional
    public void deleteProfileImage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        String imageUrl = user.getProfileImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            s3Service.deleteFile(imageUrl); // S3에서 이미지 삭제
            user.updateProfileImageUrl(null); // DB에서 URL 제거
        }
    }

    @PerformanceCheck
    public Page<UserResponse> searchUsers(String nickName, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return userRepository.findByNickName(nickName, pageable);
    }
}
