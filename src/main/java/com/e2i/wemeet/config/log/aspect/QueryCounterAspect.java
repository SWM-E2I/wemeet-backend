package com.e2i.wemeet.config.log.aspect;

import com.e2i.wemeet.config.log.module.ConnectionInvocationHandler;
import com.e2i.wemeet.config.log.module.QueryCounter;
import java.lang.reflect.Proxy;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@RequiredArgsConstructor
@Aspect
public class QueryCounterAspect {

    private final QueryCounter apiQueryCounter;

    /*
     * database 와 connection 이 일어날 때마다 queryCount를 늘림
     * */
    @Around("execution(* javax.sql.DataSource.getConnection())")
    public Object getConnection(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        final Object connection = proceedingJoinPoint.proceed();
        return Proxy.newProxyInstance(
            connection.getClass().getClassLoader(),
            connection.getClass().getInterfaces(),
            new ConnectionInvocationHandler(connection, apiQueryCounter)
        );
    }

}
