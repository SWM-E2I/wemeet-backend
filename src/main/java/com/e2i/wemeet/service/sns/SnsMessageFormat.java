package com.e2i.wemeet.service.sns;

public enum SnsMessageFormat {
    MEETING_REQUEST(
        "[위밋] 너희를 만나고 싶어 하는 팀이 있어! 🕊️\n" +
            "지금 바로 위밋에서 확인해 봐!"
    ),
    MEETING_ACCEPT(
        "[위밋] %s도 너희 팀이 좋대! 💘\n" +
            "지금 바로 위밋에서 확인해 봐!"
    );

    private final String messageFormat;

    SnsMessageFormat(String messageFormat) {
        this.messageFormat = messageFormat;
    }

    public static String getMeetingAcceptMessage(final String leaderNickname) {
        return String.format(MEETING_ACCEPT.messageFormat, leaderNickname);
    }

    public static String getMeetingRequestMessage() {
        return MEETING_REQUEST.messageFormat;
    }
}
