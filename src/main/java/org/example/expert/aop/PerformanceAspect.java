package org.example.expert.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class PerformanceAspect {

    @Around("@annotation(org.example.expert.aop.annotation.PerformanceCheck)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long end = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();
        log.info("{} 실행 시간: {}ms", methodName, (end - start));

        return result;
    }
}
