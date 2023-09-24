package com.e2i.wemeet.controller.team;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.e2i.wemeet.dto.request.team.DeleteTeamImageRequestDto;
import com.e2i.wemeet.support.config.AbstractControllerUnitTest;
import com.e2i.wemeet.support.config.WithCustomMockUser;
import com.e2i.wemeet.support.fixture.TeamImagesFixture;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

class TeamImageControllerTest extends AbstractControllerUnitTest {

    @DisplayName("팀 이미지를 업로드할 수 있다.")
    @WithCustomMockUser
    @Test
    void upload() throws Exception {
        // given
        MockMultipartFile images = new MockMultipartFile("images", "test.png", "image/png",
            "test".getBytes());
        given(teamImageService.uploadTeamImage(anyLong(), anyList()))
            .willReturn(TeamImagesFixture.BASIC_TEAM_IMAGE.getTeamImages());

        // when
        ResultActions perform = mockMvc.perform(
            multipart("/v1/team/image")
                .file(images)
                .with(csrf())
                .contentType(MULTIPART_FORM_DATA));

        // then
        perform
            .andExpectAll(
                status().isOk(),
                jsonPath("$.status").value("SUCCESS"),
                jsonPath("$.message").value("Team Image Upload Success"),
                jsonPath("$.data").isArray()
            );

        writeRestDocsUploadImages(perform);
    }

    @DisplayName("팀 이미지를 수정할 수 있다.")
    @WithCustomMockUser
    @Test
    void update() throws Exception {
        // given
        MockMultipartFile images = new MockMultipartFile("images", "test.png", "image/png",
            "test".getBytes());
        given(teamImageService.updateTeamImage(anyLong(), anyList()))
            .willReturn(TeamImagesFixture.BASIC_TEAM_IMAGE.getTeamImages());

        // when
        MockMultipartHttpServletRequestBuilder builder =
            RestDocumentationRequestBuilders.multipart("/v1/team/image");
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        ResultActions perform = mockMvc.perform(
            builder
                .file(images)
                .with(csrf())
                .contentType(MULTIPART_FORM_DATA));

        // then
        perform
            .andExpectAll(
                status().isOk(),
                jsonPath("$.status").value("SUCCESS"),
                jsonPath("$.message").value("Team Image Update Success"),
                jsonPath("$.data").isArray()
            );

        writeRestDocsUpdateImages(perform);
    }

    @DisplayName("팀 이미지를 삭제할 수 있다.")
    @WithCustomMockUser
    @Test
    void delete() throws Exception {
        // given
        DeleteTeamImageRequestDto requestDto = new DeleteTeamImageRequestDto(List.of(
            "https://image.s3.ap-northeast-2.amazonaws.com/v1/teams/1.png",
            "https://image.s3.ap-northeast-2.amazonaws.com/v1/teams/2.png"
        ));
        willDoNothing().
            given(teamImageService).deleteTeamImage(anyList());

        // when
        ResultActions perform = mockMvc.perform(RestDocumentationRequestBuilders.delete("/v1/team/image")
            .with(csrf())
            .contentType(APPLICATION_JSON)
            .content(toJson(requestDto)));

        // then
        perform
            .andExpectAll(
                status().isOk(),
                jsonPath("$.status").value("SUCCESS"),
                jsonPath("$.message").value("Delete Team Image Success"),
                jsonPath("$.data").doesNotExist()
            );

        writeRestDocsDeleteImages(perform);
    }

    private void writeRestDocsUploadImages(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("팀 이미지 업로드",
                    ResourceSnippetParameters.builder()
                        .tag("팀 관련 API")
                        .summary("팀 이미지를 업로드합니다.")
                        .description("""
                            팀 이미지를 업로드 합니다.
                            새로 업로드하는 이미지는 기존 이미지 뒤에 위치합니다.
                            한 팀당 최대 10개의 이미지를 업로드 할 수 있습니다.
                                                        
                            multipart/form-data 데이터로 보내주어야함 (data는 json!)
                            "images": File[], // 기존 이미지 + 업로드 이미지 개수가 10개를 넘지 않아야 함
                            """
                        ),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.ARRAY).description("업로드된 이미지 URL 목록을 반환합니다.")
                    )
                )
            );
    }

    private void writeRestDocsUpdateImages(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("팀 이미지 업데이트",
                    ResourceSnippetParameters.builder()
                        .tag("팀 관련 API")
                        .summary("팀 이미지를 업데이트합니다.")
                        .description("""
                            팀 이미지를 업데이트합니다.
                            기존 이미지를 모두 삭제한 뒤, 새로운 이미지를 저장합니다.
                                                        
                            multipart/form-data 데이터로 보내주어야함 (data는 json!)
                            "images": File[], // 최소 1장, 최대 10장
                            """
                        ),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.ARRAY).description("업로드된 이미지 URL 목록을 반환합니다.")
                    )
                )
            );
    }

    private void writeRestDocsDeleteImages(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("팀 이미지 삭제",
                    ResourceSnippetParameters.builder()
                        .tag("팀 관련 API")
                        .summary("팀 이미지를 삭제합니다.")
                        .description("""
                            팀 이미지를 삭제합니다.
                            삭제할 url을 전달받아, 해당 이미지를 팀에서 삭제합니다.
                            """
                        ),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.NULL).description("data는 아무것도 반환되지 않습니다.")
                    )
                )
            );
    }
}