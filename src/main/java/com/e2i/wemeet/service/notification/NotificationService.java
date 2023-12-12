package com.e2i.wemeet.service.notification;

public interface NotificationService {

    /*
     * 전체 사용자 푸시 알림 전송
     */
    void sendToAllMembers(String title, String body);

    /*
     * 팀이 없는 사용자 푸시 알림 전송
     */
    void sendToMembersWithoutTeam(String title, String body);
}
