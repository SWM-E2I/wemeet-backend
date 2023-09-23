package com.e2i.wemeet.controller.credit;

import com.e2i.wemeet.config.resolver.member.MemberId;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.service.credit.CreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1/credit")
@RestController
public class CreditController {

    private final CreditService creditService;

    @GetMapping
    public ResponseDto<Integer> getCredit(@MemberId Long memberId) {
        int credit = creditService.getCredit(memberId);

        return ResponseDto.success("Getting User's Credit Success", credit);
    }

}
