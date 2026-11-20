package kr.hs.dgsw.springproject.domain.auth.controller;

import kr.hs.dgsw.springproject.domain.auth.service.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@DisplayName("인증 API")
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmailService emailService;

    @Test
    @DisplayName("이메일 인증 코드 발송 요청에 성공하면 200을 반환한다")
    void sendEmailVerificationCode_returns200() throws Exception {
        mockMvc.perform(post("/auth/email/code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"user@example.com"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));

        verify(emailService).sendCode("user@example.com");
    }

    @Test
    @DisplayName("이메일 인증 코드 확인 요청에 성공하면 200을 반환한다")
    void verifyEmail_returns200() throws Exception {
        mockMvc.perform(post("/auth/email/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"user@example.com","code":"123456"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));

        verify(emailService).verifyCode("user@example.com", "123456");
    }
}
