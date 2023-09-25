package com.e2i.wemeet.controller.member;

import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.e2i.wemeet.dto.request.member.RecommenderRequestDto;
import com.e2i.wemeet.support.config.AbstractControllerUnitTest;
import com.e2i.wemeet.support.config.WithCustomMockUser;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

class RecommendControllerTest extends AbstractControllerUnitTest {

    @DisplayName("입력한 번호가 형식에 어긋날 경우 요청에 실패한다.")
    @WithCustomMockUser
    @Test
    void recommendWithInvalidPhone() throws Exception {
        // given
        final RecommenderRequestDto requestDto = new RecommenderRequestDto("01012345678");
        willDoNothing()
            .given(recommendService).recommend(1L, "01012345678");

        // when
        ResultActions perform = mockMvc.perform(post("/v1/recommend")
            .contentType(APPLICATION_JSON)
            .content(toJson(requestDto)));

        // then
        perform
            .andExpectAll(
                status().isOk(),
                jsonPath("$.status").value("FAIL"),
                jsonPath("$.message").value("형식에 맞지 않는 phoneNumber입니다."),
                jsonPath("$.data").doesNotExist()
            );
        verify(recommendService, times(0))
            .recommend(1L, "+821012345678");
    }

    @WithCustomMockUser
    @DisplayName("추천인의 휴대폰 번호를 입력하여 추천인을 등록할 수 있다.")
    @Test
    void recommendWithRecommenderPhone() throws Exception {
        // given
        final RecommenderRequestDto requestDto = new RecommenderRequestDto("+821012345678");
        willDoNothing()
            .given(recommendService).recommend(1L, "+821012345678");

        // when
        ResultActions perform = mockMvc.perform(post("/v1/recommend")
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(requestDto)));

        // then
        perform
            .andExpectAll(
                status().isOk(),
                jsonPath("$.status").value("SUCCESS"),
                jsonPath("$.message").value("Recommend Success"),
                jsonPath("$.data").doesNotExist()
            );
        verify(recommendService).recommend(1L, "+821012345678");

        writeRestDocsRecommend(perform);
    }

    public void writeRestDocsRecommend(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("추천인 등록",
                    ResourceSnippetParameters.builder()
                        .tag("회원 관련 API")
                        .summary("추천인을 등록합니다.")
                        .description(
                            """
                                    위밋을 추천해준 추천인의 전화번호를 입력하여 추천인으로 등록합니다.
                                    계정당 1번만 가능하고 추천인 상대에게 20코인을 지급합니다.
                                """),
                    requestFields(
                        fieldWithPath("phoneNumber").type(JsonFieldType.STRING)
                            .description("위밋을 추천해준 추천인 전화번호 (+82101234xxxx)")
                    ),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터는 없습니다")
                    )
                ));
    }
}