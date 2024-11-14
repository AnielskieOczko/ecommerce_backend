package com.rj.ecommerce_backend.securityconfig;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ResourceOwnerAspect {

    private final SecurityService securityService;

    public void checkResourceOwnership(JoinPoint joinPoint, ResourceOwner resourceOwner) {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = methodSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        Long resourceId = null;
        for (int i = 0; i < parameterNames.length; i++) {
            if (parameterNames[i].equals(resourceOwner.parameterName())) {
                resourceId = (Long) args[i];
                break;
            }
        }

        if (resourceId == null) {
            throw new IllegalArgumentException("Resource ID not found");
        }

        // Check if current user owns the resource
        if (!securityService.hasAccessToResource(resourceId)) {
            throw new AccessDeniedException("You don't have permission to access this resource");
        }


    }
}
