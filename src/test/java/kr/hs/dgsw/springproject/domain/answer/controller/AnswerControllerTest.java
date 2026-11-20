package kr.hs.dgsw.springproject.domain.answer.controller;

import kr.hs.dgsw.springproject.domain.answer.dto.response.AnswerResponse;
import kr.hs.dgsw.springproject.domain.answer.service.AnswerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnswerController.class)
@DisplayName("답변 API")
class AnswerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnswerService answerService;

    @Test
    @DisplayName("답변 등록 요청에 성공하면 201과 답변 정보를 반환한다")
    void createAnswer_returns201AndAnswerResponse() throws Exception {
        when(answerService.create(eq(1L), any())).thenReturn(answerResponse());

        mockMvc.perform(post("/questions/1/answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"content":"답변 내용","userId":2}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.content").value("답변 내용"));
    }

    @Test
    @DisplayName("답변 내용이 비어 있으면 400을 반환한다")
    void createAnswer_withBlankContent_returns400() throws Exception {
        mockMvc.perform(post("/questions/1/answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"content":"","userId":2}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(answerService);
    }

    @Test
    @DisplayName("답변 목록 조회 요청에 성공하면 200과 목록을 반환한다")
    void getAnswers_returns200AndAnswerList() throws Exception {
        when(answerService.getAllByQuestionId(1L)).thenReturn(List.of(answerResponse()));

        mockMvc.perform(get("/questions/1/answers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    @DisplayName("답변 수정 요청에 성공하면 200과 수정된 답변을 반환한다")
    void updateAnswer_returns200AndAnswerResponse() throws Exception {
        when(answerService.update(eq(1L), any())).thenReturn(answerResponse());

        mockMvc.perform(put("/answers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"content":"수정된 답변"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @DisplayName("답변 삭제 요청에 성공하면 204를 반환한다")
    void deleteAnswer_returns204() throws Exception {
        doNothing().when(answerService).delete(1L);

        mockMvc.perform(delete("/answers/1"))
                .andExpect(status().isNoContent());

        verify(answerService).delete(1L);
    }

    @Test
    @DisplayName("답변 채택 요청에 성공하면 200과 채택된 답변을 반환한다")
    void acceptAnswer_returns200AndAnswerResponse() throws Exception {
        when(answerService.accept(1L)).thenReturn(answerResponse());

        mockMvc.perform(put("/answers/1/accept"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.accepted").value(true));
    }

    private AnswerResponse answerResponse() {
        return new AnswerResponse(1L, "답변 내용", true, 1L, 2L);
    }
}
