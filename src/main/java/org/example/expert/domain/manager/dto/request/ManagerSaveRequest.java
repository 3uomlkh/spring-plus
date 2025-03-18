package org.example.expert.domain.manager.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.user.entity.User;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ManagerSaveRequest {

    @NotNull
    private Long managerUserId; // 일정 작상자가 배치하는 유저 id
    private User resolvedManager;

    public void setResolvedManager(User managerUser) {
        this.resolvedManager = managerUser;
    }

}
