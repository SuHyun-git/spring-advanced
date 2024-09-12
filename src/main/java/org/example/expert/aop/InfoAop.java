package org.example.expert.aop;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Slf4j
@Aspect
@Component
public class InfoAop {

    @Pointcut("@annotation(org.example.expert.anno.InfoAnnotation)")
    private void infoAnnotation() {}

    /**
     * 어드바이스: 어노테이션 범위 기반
     */
    @Around("infoAnnotation()")
    public Object info(ProceedingJoinPoint joinPoint) throws Throwable {
        //측정 시작
        String currentTime = LocalDateTime.now().toString();

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        Long userId = (Long) request.getAttribute("userId");

        try {
            Object result = joinPoint.proceed();
            return result;
        } finally {
            // 측정 완료
            log.info("::: User Id: {}", userId);
            log.info("::: API 실행시간: {}ms", currentTime);
            log.info("::: API URL: {}", request.getRequestURI());
        }
    }
}
