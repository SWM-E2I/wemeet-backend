package com.e2i.wemeet.config.log.aspect;

import com.e2i.wemeet.security.model.MemberPrincipal;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.context.SecurityContextHolder;

@Aspect
public class ControllerLogAspect {

    private static final Logger log = LogManager.getLogger(ControllerLogAspect.class);
    private static final String CONTROLLER_LOG_FORMAT_BEFORE_PROCEED = "[CONTROLLER-START]:: MEMBER_ID: {}, CLASS: {}, METHOD: {}, PARAMS: {}";
    private static final String CONTROLLER_LOG_FORMAT_AFTER_PROCEED = "[CONTROLLER-END]:: MEMBER_ID: {}, CLASS: {}, METHOD: {}, RETURN_TYPE: {}, RETURN_VALUE: {}";
    private static final String NOT_AUTHENTICATED = "none";

    @Around("execution(* com.e2i.wemeet.controller..*.*(..))")
    public Object verify(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = signature.getMethod().getName();
        Map<String, Object> parameters = getParameters(joinPoint, signature);
        String memberId = getMemberId();

        log.info(CONTROLLER_LOG_FORMAT_BEFORE_PROCEED, memberId, className, methodName, parameters);
        Object proceed = joinPoint.proceed();

        log.info(CONTROLLER_LOG_FORMAT_AFTER_PROCEED, memberId, className, methodName, signature.getReturnType().getSimpleName(), proceed);

        return proceed;
    }

    private String getMemberId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof MemberPrincipal memberPrincipal)) {
            return NOT_AUTHENTICATED;
        }
        return String.valueOf(memberPrincipal.getMemberId());
    }

    private Map<String, Object> getParameters(JoinPoint joinPoint, MethodSignature signature) {
        Map<String, Object> parameterMap = new HashMap<>();

        String[] parameterNames = signature.getParameterNames();
        Object[] parameters = joinPoint.getArgs();

        for (int i = 0; i < parameterNames.length; i++) {
            parameterMap.put(parameterNames[i], parameters[i]);
        }
        return parameterMap;
    }

}
