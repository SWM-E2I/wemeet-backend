package com.e2i.wemeet.domain.member;

/*
* GUEST - 웹 인증 회원
* USER - 앱 인증 회원
* MANAGER - 팀장
* ADMIN - 관리자
* */
public enum Role {
    GUEST, USER, MANAGER, ADMIN;

    public static final String prefix = "ROLE_";

    public String getRoleAttachedPrefix() {
        return prefix.concat(this.name());
    }

    /*
    * USER -> ROLE_USER
    * -> 인가 로직에 필요함
    * */
    public static String getRoleAttachedPrefix(final String role) {
        if (role.startsWith(prefix)) {
            return role;
        }
        return prefix.concat(role);
    }

    // ROLE_USER -> USER
    public static String removePrefix(String role) {
        return role.replace(prefix, "");
    }
}
