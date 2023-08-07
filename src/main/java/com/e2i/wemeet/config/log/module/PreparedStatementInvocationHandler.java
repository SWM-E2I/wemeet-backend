package com.e2i.wemeet.config.log.module;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import org.springframework.web.context.request.RequestContextHolder;

public class PreparedStatementInvocationHandler implements InvocationHandler {

    private final Object preparedStatement;
    private final QueryCounter queryCounter;

    public PreparedStatementInvocationHandler(Object preparedStatement, QueryCounter queryCounter) {
        this.preparedStatement = preparedStatement;
        this.queryCounter = queryCounter;
    }

    /*
     * sql 을 execute 한다면 queryCount를 1 증가 시킴
     * */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().contains("execute") && RequestContextHolder.getRequestAttributes() != null) {
            queryCounter.increase();
        }
        return method.invoke(preparedStatement, args);
    }
}
