package kr.hs.dgsw.springproject.domain.user.controller;

import kr.hs.dgsw.springproject.domain.user.domain.User;
import kr.hs.dgsw.springproject.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@DisplayName("회원 API")
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("회원 생성 요청에 성공하면 201과 회원 정보를 반환한다")
    void createUser_returns201AndUserResponse() throws Exception {
        when(userService.create(any())).thenReturn(
                User.builder()
                        .name("테스트 사용자")
                        .email("user@example.com")
                        .password("password")
                        .build()
        );

        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"테스트 사용자","email":"user@example.com","password":"password"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.name").value("테스트 사용자"))
                .andExpect(jsonPath("$.data.email").value("user@example.com"));
    }

    @Test
    @DisplayName("회원 조회 요청에 성공하면 200과 회원 정보를 반환한다")
    void findById_returns200AndUserResponse() throws Exception {
        when(userService.findById(1L)).thenReturn(
                User.builder()
                        .name("테스트 사용자")
                        .email("user@example.com")
                        .password("password")
                        .build()
        );

        mockMvc.perform(get("/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.name").value("테스트 사용자"))
                .andExpect(jsonPath("$.data.email").value("user@example.com"));
    }
}
