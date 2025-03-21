package org.example.expert.domain.user.dto.response;

import lombok.Getter;
import org.example.expert.domain.user.entity.User;

@Getter
public class UserResponse {

    private final Long id;
    private final String email;
    private final String nickName;
    private final String profileImageUrl;

    public UserResponse(Long id, String email, String nickName, String profileImageUrl) {
        this.id = id;
        this.email = email;
        this.nickName = nickName;
        this.profileImageUrl = profileImageUrl;
    }

    public static UserResponse of(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickName(),
                user.getProfileImageUrl()
        );
    }
}
