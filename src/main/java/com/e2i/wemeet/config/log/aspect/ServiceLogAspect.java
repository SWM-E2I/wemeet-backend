package com.e2i.wemeet.config.log.aspect;

import com.e2i.wemeet.config.log.MdcKey;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

@Slf4j
@Aspect
public class ServiceLogAspect {

    private static final String SERVICE_LOG_FORMAT_BEFORE_PROCEED = "[SERVICE-START] :: MEMBER_ID: {}, CLASS: {}, METHOD: {}, PARAMS: {}";
    private static final String SERVICE_LOG_FORMAT_AFTER_PROCEED = "[SERVICE-END] :: MEMBER_ID: {}, CLASS: {}, METHOD: {}, RETURN_TYPE: {}, RETURN_VALUE: {}";
    private static final String NOT_AUTHENTICATED = "none";

    @Around("execution(* com.e2i.wemeet.service..*.*(..))")
    public Object verify(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = signature.getMethod().getName();
        String memberId = getMemberId();

        log.info(SERVICE_LOG_FORMAT_BEFORE_PROCEED, memberId, className, methodName, getParameters(joinPoint, signature));
        Object proceed = joinPoint.proceed();

        log.info(SERVICE_LOG_FORMAT_AFTER_PROCEED, memberId, className, methodName, signature.getReturnType().getSimpleName(), proceed);
        return proceed;
    }

    private String getMemberId() {
        String memberId = MDC.get(MdcKey.MEMBER_ID.getKey());
        if (!StringUtils.hasText(memberId)) {
            return NOT_AUTHENTICATED;
        }
        return memberId;
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
