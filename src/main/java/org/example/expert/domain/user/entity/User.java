package org.example.expert.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.entity.Timestamped;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User extends Timestamped {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String email;
    private String password;
    private String nickName;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;
    private String profileImageUrl;

    public User(String email, String password, UserRole userRole, String nickName) {
        this.email = email;
        this.password = password;
        this.userRole = userRole;
        this.nickName = nickName;
    }

    private User(Long id, String email, UserRole userRole, String nickName) {
        this.id = id;
        this.email = email;
        this.userRole = userRole;
        this.nickName = nickName;
    }

    public static User fromAuthUser(AuthUser authUser) {
        String role = authUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("권한이 없습니다."));
        return new User(authUser.getId(), authUser.getEmail(), UserRole.valueOf(role), authUser.getNickName());
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void updateRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public void updateProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
