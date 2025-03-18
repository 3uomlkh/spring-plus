package org.example.expert.log.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.user.entity.User;
import org.example.expert.log.entity.Log;
import org.example.expert.log.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(String status, User user, User manager, Long todoId, String errorMessage) {
        Log log = Log.builder()
                .status(status)
                .userId(user != null ? user.getId() : null)
                .userEmail(user != null ? user.getEmail() : "Unknown")
                .managerId(manager != null ? manager.getId() : null)
                .managerEmail(manager != null ? manager.getEmail() : "N/A")
                .todoId(todoId)
                .errorMessage(errorMessage)
                .build();

        logRepository.save(log);
    }
}
