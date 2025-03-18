package org.example.expert.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.user.entity.User;
import org.example.expert.log.service.LogService;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ManagerExceptionLoggingAspect {

    private final LogService logService;

    @AfterThrowing(pointcut = "execution(* org.example.expert.domain.manager.service.ManagerService.*(..))", throwing = "ex")
    public void logManagerException(JoinPoint joinPoint, Exception ex) {
        Object[] args = joinPoint.getArgs();

        long todoId = 0;
        User user = null;
        User managerUser = null;

        for (Object o : args) {
            if (o instanceof AuthUser) {
                user = User.fromAuthUser((AuthUser) o);
            }

            if (o instanceof Long) {
                todoId = (long) o;
            }

            if (o instanceof ManagerSaveRequest) {
                managerUser = ((ManagerSaveRequest) o).getResolvedManager();
            }
        }

        // 실패 로그 저장
        logService.saveLog("FAILURE", user, managerUser, todoId, ex.getMessage());
    }
}
