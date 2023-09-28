package com.e2i.wemeet.controller.member;

import com.e2i.wemeet.config.resolver.member.MemberId;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.service.member.BlockService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1/member/block")
@RestController
public class BlockController {

    private final BlockService blockService;

    @PostMapping("/{blockMemberId}")
    public ResponseDto<Long> blockMember(@MemberId Long memberId, @PathVariable Long blockMemberId) {
        final Long blockedMemberId = blockService.block(memberId, blockMemberId);
        return ResponseDto.success("Block Member Success", blockedMemberId);
    }

    @GetMapping
    public ResponseDto<List<Long>> readBlockMembers(@MemberId Long memberId) {
        final List<Long> blockList = blockService.readBlockList(memberId);
        return ResponseDto.success("Read Block Member Success", blockList);
    }
}
