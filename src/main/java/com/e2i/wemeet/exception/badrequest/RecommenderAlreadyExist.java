package com.e2i.wemeet.exception.badrequest;

import static com.e2i.wemeet.exception.ErrorCode.RECOMMENDER_ALREADY_EXIST;

public class RecommenderAlreadyExist extends BadRequestException {

    public RecommenderAlreadyExist() {
        super(RECOMMENDER_ALREADY_EXIST);
    }

}
