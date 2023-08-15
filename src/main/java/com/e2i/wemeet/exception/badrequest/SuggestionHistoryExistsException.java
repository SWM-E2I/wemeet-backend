package com.e2i.wemeet.exception.badrequest;

import com.e2i.wemeet.exception.ErrorCode;

public class SuggestionHistoryExistsException extends BadRequestException {

    public SuggestionHistoryExistsException() {
        super(ErrorCode.SUGGESTION_HISTORY_EXISTS);
    }

    public SuggestionHistoryExistsException(ErrorCode errorCode) {
        super(errorCode);
    }
}
