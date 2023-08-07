package com.e2i.wemeet.config.log.module;

/*
 * 한 요청에 발생 하는 Query 의 개수를 기록하기 위한 component
 * Bean 생명 주기 -> RequestScope
 */
public class QueryCounter {

    private int count;

    public void increase() {
        count++;
    }

    public int getQueryCount() {
        return count;
    }
}
