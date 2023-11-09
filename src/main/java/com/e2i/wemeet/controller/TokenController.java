package com.e2i.wemeet.controller;

import com.e2i.wemeet.config.resolver.member.MemberId;
import com.e2i.wemeet.dto.request.token.PushTokenRequestDto;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.dto.response.ResponseStatus;
import com.e2i.wemeet.dto.response.persist.PersistResponseDto;
import com.e2i.wemeet.security.token.handler.AccessTokenHandler;
import com.e2i.wemeet.service.token.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1")
@RestController
public class TokenController {

    private final TokenService tokenService;
    private final AccessTokenHandler accessTokenHandler;


    @GetMapping("/auth/persist")
    public ResponseDto<PersistResponseDto> persist(@MemberId Long memberId) {
        PersistResponseDto result = tokenService.persistLogin(memberId);
        return new ResponseDto<>(ResponseStatus.SUCCESS, "Persist Login Success", result);
    }

    @PostMapping("/push")
    public ResponseDto<Void> savePushToken(@RequestBody PushTokenRequestDto requestDto,
        @RequestHeader(name = "AccessToken", defaultValue = "") String accessToken) {
        Long memberId = accessTokenHandler.extractMemberId(accessToken).orElseGet(() -> null);
        tokenService.savePushToken(requestDto.pushToken(), memberId);

        return ResponseDto.success("Push Token Save Success");
    }

}
