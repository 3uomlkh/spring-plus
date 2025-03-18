package org.example.expert.log.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.common.entity.Timestamped;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "logs")
public class Log extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String status;

    private Long userId;
    private String userEmail;

    private Long managerId;
    private String managerEmail;

    private Long todoId;
    private String errorMessage;

    @Builder
    public Log(String status, Long userId, String userEmail, Long managerId, String managerEmail, Long todoId, String errorMessage) {
        this.status = status;
        this.userId = userId;
        this.userEmail = userEmail;
        this.managerId = managerId;
        this.managerEmail = managerEmail;
        this.todoId = todoId;
        this.errorMessage = errorMessage;
    }
}
