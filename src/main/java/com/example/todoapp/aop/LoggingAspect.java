package com.example.todoapp.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("@annotation(com.example.todoapp.annotation.TrackExecutionTime)")
    public void methodAnnotatedWithTrackExecutionTime() {}

    @Pointcut("@within(com.example.todoapp.annotation.TrackExecutionTime)")
    public void classAnnotatedWithTrackExecutionTime() {}

    @Around("methodAnnotatedWithTrackExecutionTime() || classAnnotatedWithTrackExecutionTime()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        log.info("API Methodu: '{}' - Süre: {} ms ",
                joinPoint.getSignature().toShortString(),
                duration);

        return result;
    }
}