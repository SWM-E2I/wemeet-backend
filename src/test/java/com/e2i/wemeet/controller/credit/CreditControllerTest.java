package com.e2i.wemeet.controller.credit;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.e2i.wemeet.support.config.AbstractControllerUnitTest;
import com.e2i.wemeet.support.config.WithCustomMockUser;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;


class CreditControllerTest extends AbstractControllerUnitTest {

    @DisplayName("사용자의 크레딧을 가져올 수 있다.")
    @WithCustomMockUser
    @Test
    void getCredit() throws Exception {
        // given
        given(creditService.getCredit(1L))
            .willReturn(30);

        // when
        ResultActions perform = mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/credit"));

        // then
        perform
            .andExpectAll(
                status().isOk(),
                jsonPath("$.status").value("SUCCESS"),
                jsonPath("$.message").value("Getting User's Credit Success"),
                jsonPath("$.data").value(30)
            );
        verify(creditService).getCredit(1L);

        getCreditWriteRestDocs(perform);
    }

    private void getCreditWriteRestDocs(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("크레딧 조회",
                    ResourceSnippetParameters.builder()
                        .tag("크레딧 관련 API")
                        .summary("로그인한 사용자의 크레딧을 조회합니다")
                        .description(
                            """
                                    로그인한 사용자의 크레딧을 조회합니다.
                                """),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.NUMBER)
                            .description("사용자의 크레딧 개수가 반환됩니다.")
                    )
                ));
    }

}