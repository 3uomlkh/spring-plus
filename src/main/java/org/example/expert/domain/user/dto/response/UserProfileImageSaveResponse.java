package org.example.expert.domain.user.dto.response;

import lombok.Getter;

@Getter
public class UserProfileImageSaveResponse {

    private final String profileImageUrl;

    public UserProfileImageSaveResponse(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
