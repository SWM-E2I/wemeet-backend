package com.e2i.wemeet.config.log.module;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/*
 * Jdbc Connection 을 받아 오는 동적 프록시 객체
 * */
public class ConnectionInvocationHandler implements InvocationHandler {

    private final Object connection;
    private final QueryCounter queryCounter;

    public ConnectionInvocationHandler(Object connection, QueryCounter queryCounter) {
        this.connection = connection;
        this.queryCounter = queryCounter;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final Object result = method.invoke(connection, args);
        if (method.getName().equals("prepareStatement")) {
            return Proxy.newProxyInstance(
                result.getClass().getClassLoader(),
                result.getClass().getInterfaces(),
                new PreparedStatementInvocationHandler(result, queryCounter)
            );
        }
        return result;
    }
}
