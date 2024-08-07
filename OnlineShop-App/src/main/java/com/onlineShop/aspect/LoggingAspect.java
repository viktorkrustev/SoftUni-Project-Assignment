package com.onlineshop.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* com.onlineshop.service.UserService.*(..))")
    public void logBeforeUserServiceMethods(JoinPoint joinPoint) {
        System.out.println("Executing method: " + joinPoint.getSignature().toShortString());
    }

    @After("execution(* com.onlineshop.service.OrderService.*(..))")
    public void logAfterOrderServiceMethods(JoinPoint joinPoint) {
        System.out.println("Finished executing method: " + joinPoint.getSignature().toShortString());
    }
}
