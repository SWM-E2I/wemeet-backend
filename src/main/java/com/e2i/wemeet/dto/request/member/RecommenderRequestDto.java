package com.e2i.wemeet.dto.request.member;

import com.e2i.wemeet.util.validator.bean.PhoneValid;
import jakarta.validation.constraints.NotNull;

public record RecommenderRequestDto(

    @NotNull
    @PhoneValid
    String phoneNumber

) {

}
