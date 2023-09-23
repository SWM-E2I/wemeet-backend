package com.e2i.wemeet.controller;

import com.e2i.wemeet.config.resolver.member.MemberId;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.dto.response.ResponseStatus;
import com.e2i.wemeet.dto.response.persist.PersistResponseDto;
import com.e2i.wemeet.service.token.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1/auth")
@RestController
public class TokenController {

    private final TokenService tokenService;

    @GetMapping("/persist")
    public ResponseDto<PersistResponseDto> persist(@MemberId Long memberId) {
        PersistResponseDto result = tokenService.persistLogin(memberId);
        return new ResponseDto<>(ResponseStatus.SUCCESS, "Persist Login Success", result);
    }
}
