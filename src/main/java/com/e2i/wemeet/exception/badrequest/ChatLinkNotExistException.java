package com.e2i.wemeet.exception.badrequest;

import com.e2i.wemeet.exception.ErrorCode;

public class ChatLinkNotExistException extends BadRequestException {

    public ChatLinkNotExistException() {
        super(ErrorCode.CHAT_LINK_NOT_EXIST);
    }

    public ChatLinkNotExistException(ErrorCode code) {
        super(code);
    }

}
