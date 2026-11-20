package kr.hs.dgsw.springproject.domain.question.controller;

import kr.hs.dgsw.springproject.domain.question.domain.Question;
import kr.hs.dgsw.springproject.domain.question.exception.QuestionStatusCode;
import kr.hs.dgsw.springproject.domain.question.service.QuestionService;
import kr.hs.dgsw.springproject.domain.user.domain.User;
import kr.hs.dgsw.springproject.domain.user.exception.UserStatusCode;
import kr.hs.dgsw.springproject.global.exception.ApplicationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuestionController.class)
@DisplayName("질문 API")
class QuestionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuestionService questionService;

    private static User user() {
        User user = User.builder()
                .name("테스트 사용자")
                .email("user@example.com")
                .password("password")
                .build();
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }

    private static Question question(Long id, String title, String content) {
        Question question = new Question(title, content, user());
        ReflectionTestUtils.setField(question, "id", id);
        return question;
    }

    @Nested
    @DisplayName("POST /questions - 질문 등록")
    class CreateQuestion {

        @Test
        @DisplayName("질문 등록에 성공하면 201과 질문 정보를 반환한다")
        void createQuestion_returns201AndQuestionResponse() throws Exception {
            when(questionService.create(any())).thenReturn(question(1L, "질문 제목", "질문 내용"));

            mockMvc.perform(post("/questions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"userId":1,"title":"질문 제목","content":"질문 내용"}
                                    """))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").value(201))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.title").value("질문 제목"))
                    .andExpect(jsonPath("$.data.content").value("질문 내용"))
                    .andExpect(jsonPath("$.data.userId").value(1))
                    .andExpect(jsonPath("$.data.userName").value("테스트 사용자"));
        }

        @Test
        @DisplayName("제목이 비어 있으면 400과 title 오류를 반환한다")
        void createQuestion_whenTitleIsBlank_returns400() throws Exception {
            mockMvc.perform(post("/questions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"userId":1,"title":" ","content":"질문 내용"}
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error.details.title").exists());

            verifyNoInteractions(questionService);
        }

        @Test
        @DisplayName("제목이 200자를 넘으면 400과 title 오류를 반환한다")
        void createQuestion_whenTitleTooLong_returns400() throws Exception {
            String longTitle = "가".repeat(201);

            mockMvc.perform(post("/questions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"userId":1,"title":"%s","content":"질문 내용"}
                                    """.formatted(longTitle)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error.details.title").exists());

            verifyNoInteractions(questionService);
        }

        @Test
        @DisplayName("내용이 비어 있으면 400과 content 오류를 반환한다")
        void createQuestion_whenContentIsBlank_returns400() throws Exception {
            mockMvc.perform(post("/questions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"userId":1,"title":"질문 제목","content":""}
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error.details.content").exists());

            verifyNoInteractions(questionService);
        }

        @Test
        @DisplayName("작성자 ID가 없으면 400과 userId 오류를 반환한다")
        void createQuestion_whenUserIdIsMissing_returns400() throws Exception {
            mockMvc.perform(post("/questions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"title":"질문 제목","content":"질문 내용"}
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error.details.userId").exists());

            verifyNoInteractions(questionService);
        }

        @Test
        @DisplayName("존재하지 않는 회원으로 등록하면 404를 반환한다")
        void createQuestion_whenUserDoesNotExist_returns404() throws Exception {
            when(questionService.create(any()))
                    .thenThrow(new ApplicationException(UserStatusCode.USER_NOT_FOUND));

            mockMvc.perform(post("/questions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"userId":999,"title":"질문 제목","content":"질문 내용"}
                                    """))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error.message").value(UserStatusCode.USER_NOT_FOUND.getMessage()));
        }
    }

    @Nested
    @DisplayName("GET /questions - 질문 목록 및 제목 검색")
    class GetQuestions {

        @Test
        @DisplayName("검색어 없이 조회하면 200과 전체 질문 목록을 반환한다")
        void getQuestions_withoutTitle_returns200AndAllQuestions() throws Exception {
            when(questionService.findAll(isNull())).thenReturn(List.of(
                    question(2L, "자바 질문", "내용2"),
                    question(1L, "스프링 질문", "내용1")
            ));

            mockMvc.perform(get("/questions"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.data", hasSize(2)))
                    .andExpect(jsonPath("$.data[0].id").value(2))
                    .andExpect(jsonPath("$.data[0].title").value("자바 질문"))
                    .andExpect(jsonPath("$.data[0].userName").value("테스트 사용자"))
                    .andExpect(jsonPath("$.data[0].content").doesNotExist())
                    .andExpect(jsonPath("$.data[1].id").value(1));
        }

        @Test
        @DisplayName("제목으로 검색하면 검색어를 서비스에 전달하고 결과를 반환한다")
        void getQuestions_withTitle_passesTitleToService() throws Exception {
            when(questionService.findAll("스프링")).thenReturn(List.of(
                    question(1L, "스프링 질문", "내용1")
            ));

            mockMvc.perform(get("/questions").param("title", "스프링"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data", hasSize(1)))
                    .andExpect(jsonPath("$.data[0].title").value("스프링 질문"));

            verify(questionService).findAll("스프링");
        }

        @Test
        @DisplayName("검색 결과가 없으면 200과 빈 목록을 반환한다")
        void getQuestions_whenNoResult_returns200AndEmptyList() throws Exception {
            when(questionService.findAll("파이썬")).thenReturn(List.of());

            mockMvc.perform(get("/questions").param("title", "파이썬"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /questions/{id} - 질문 상세 조회")
    class GetQuestion {

        @Test
        @DisplayName("질문 조회에 성공하면 200과 질문 상세 정보를 반환한다")
        void getQuestion_returns200AndQuestionResponse() throws Exception {
            when(questionService.findById(1L)).thenReturn(question(1L, "질문 제목", "질문 내용"));

            mockMvc.perform(get("/questions/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.title").value("질문 제목"))
                    .andExpect(jsonPath("$.data.content").value("질문 내용"))
                    .andExpect(jsonPath("$.data.userId").value(1))
                    .andExpect(jsonPath("$.data.userName").value("테스트 사용자"));
        }

        @Test
        @DisplayName("존재하지 않는 질문을 조회하면 404를 반환한다")
        void getQuestion_whenQuestionDoesNotExist_returns404() throws Exception {
            when(questionService.findById(999L))
                    .thenThrow(new ApplicationException(QuestionStatusCode.QUESTION_NOT_FOUND));

            mockMvc.perform(get("/questions/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error.message").value(QuestionStatusCode.QUESTION_NOT_FOUND.getMessage()));
        }

        @Test
        @DisplayName("ID가 숫자가 아니면 400을 반환한다")
        void getQuestion_whenIdIsNotNumber_returns400() throws Exception {
            mockMvc.perform(get("/questions/abc"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));

            verifyNoInteractions(questionService);
        }
    }

    @Nested
    @DisplayName("PUT /questions/{id} - 질문 수정")
    class UpdateQuestion {

        @Test
        @DisplayName("질문 수정에 성공하면 200과 수정된 질문 정보를 반환한다")
        void updateQuestion_returns200AndUpdatedQuestion() throws Exception {
            when(questionService.update(eq(1L), any()))
                    .thenReturn(question(1L, "변경된 제목", "변경된 내용"));

            mockMvc.perform(put("/questions/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"title":"변경된 제목","content":"변경된 내용"}
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.title").value("변경된 제목"))
                    .andExpect(jsonPath("$.data.content").value("변경된 내용"));
        }

        @Test
        @DisplayName("제목이 비어 있으면 400을 반환하고 수정하지 않는다")
        void updateQuestion_whenTitleIsBlank_returns400() throws Exception {
            mockMvc.perform(put("/questions/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"title":"","content":"변경된 내용"}
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error.details.title").exists());

            verifyNoInteractions(questionService);
        }

        @Test
        @DisplayName("내용이 비어 있으면 400을 반환하고 수정하지 않는다")
        void updateQuestion_whenContentIsBlank_returns400() throws Exception {
            mockMvc.perform(put("/questions/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"title":"변경된 제목","content":" "}
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error.details.content").exists());

            verifyNoInteractions(questionService);
        }

        @Test
        @DisplayName("존재하지 않는 질문을 수정하면 404를 반환한다")
        void updateQuestion_whenQuestionDoesNotExist_returns404() throws Exception {
            when(questionService.update(eq(999L), any()))
                    .thenThrow(new ApplicationException(QuestionStatusCode.QUESTION_NOT_FOUND));

            mockMvc.perform(put("/questions/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"title":"변경된 제목","content":"변경된 내용"}
                                    """))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error.message").value(QuestionStatusCode.QUESTION_NOT_FOUND.getMessage()));
        }
    }

    @Nested
    @DisplayName("DELETE /questions/{id} - 질문 삭제")
    class DeleteQuestion {

        @Test
        @DisplayName("질문 삭제에 성공하면 204와 빈 본문을 반환한다")
        void deleteQuestion_returns204() throws Exception {
            mockMvc.perform(delete("/questions/1"))
                    .andExpect(status().isNoContent())
                    .andExpect(content().string(""));

            verify(questionService).delete(1L);
        }

        @Test
        @DisplayName("존재하지 않는 질문을 삭제하면 404를 반환한다")
        void deleteQuestion_whenQuestionDoesNotExist_returns404() throws Exception {
            doThrow(new ApplicationException(QuestionStatusCode.QUESTION_NOT_FOUND))
                    .when(questionService).delete(999L);

            mockMvc.perform(delete("/questions/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error.message").value(QuestionStatusCode.QUESTION_NOT_FOUND.getMessage()));
        }
    }
}
