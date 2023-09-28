package com.e2i.wemeet.controller.member;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.e2i.wemeet.support.config.AbstractControllerUnitTest;
import com.e2i.wemeet.support.config.WithCustomMockUser;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

class BlockControllerTest extends AbstractControllerUnitTest {

    @DisplayName("사용자를 차단할 수 있다.")
    @WithCustomMockUser
    @Test
    void block() throws Exception {
        // given
        given(blockService.block(1L, 2L))
            .willReturn(2L);

        // when
        ResultActions perform = mockMvc.perform(post("/v1/member/block/{blockMemberId}", 2L));

        // then
        perform
            .andExpectAll(
                status().isOk(),
                jsonPath("$.status").value("SUCCESS"),
                jsonPath("$.message").value("Block Member Success"),
                jsonPath("$.data").value(2L)
            );

        blockAPIWriteRestDocs(perform);
    }

    @DisplayName("차단한 사용자의 ID 목록을 조회할 수 있다.")
    @WithCustomMockUser(id = "10")
    @Test
    void readBlock() throws Exception {
        // given
        List<Long> idList = List.of(2L, 3L);
        given(blockService.readBlockList(10L))
            .willReturn(idList);

        // when
        ResultActions perform = mockMvc.perform(get("/v1/member/block"));

        // then
        perform
            .andExpectAll(
                status().isOk(),
                jsonPath("$.status").value("SUCCESS"),
                jsonPath("$.message").value("Read Block Member Success"),
                jsonPath("$.data.[0]").value(2L),
                jsonPath("$.data.[1]").value(3L)
            );

        readBlockAPIWriteRestDocs(perform);
    }

    private static void readBlockAPIWriteRestDocs(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("차단된 사용자의 목록을 조회합니다.",
                    ResourceSnippetParameters.builder()
                        .tag("차단 관련 API")
                        .summary("차단한 사용자들의 ID를 조회합니다.")
                        .description(
                            """
                                    차단한 사용자들의 ID를 조회합니다.
                                """)
                        .pathParameters(
                            parameterWithName("blockMemberId").description("차단할 사용자의 ID")
                        ),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.ARRAY)
                            .description("차단된 사용자의 ID")
                    )
                ));
    }


    private static void blockAPIWriteRestDocs(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("차단하기",
                    ResourceSnippetParameters.builder()
                        .tag("차단 관련 API")
                        .summary("지정된 사용자를 차단합니다.")
                        .description(
                            """
                                    url에서 ID로 넘겨받은 사용자를 차단합니다.
                                    차단된 사용자는 다음의 기능에서 노출되지 않습니다.
                                      - 추천 API
                                      - 받은 좋아요
                                      - 보낸 좋아요
                                      - 성사된 매칭
                                      - 보낸 미팅 신청
                                      - 받은 미팅 신청
                                      - 팀 상세 조회 불가
                                """)
                        .pathParameters(
                            parameterWithName("blockMemberId").description("차단할 사용자의 ID")
                        ),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.NUMBER)
                            .description("차단된 사용자의 ID")
                    )
                ));
    }
}