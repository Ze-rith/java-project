package kr.hs.dgsw.springproject.domain.answer.service;

import kr.hs.dgsw.springproject.domain.answer.domain.Answer;
import kr.hs.dgsw.springproject.domain.answer.dto.request.CreateAnswerRequest;
import kr.hs.dgsw.springproject.domain.answer.dto.request.UpdateAnswerRequest;
import kr.hs.dgsw.springproject.domain.answer.dto.response.AnswerResponse;
import kr.hs.dgsw.springproject.domain.answer.exception.AnswerStatusCode;
import kr.hs.dgsw.springproject.domain.answer.repository.AnswerRepository;
import kr.hs.dgsw.springproject.domain.question.domain.Question;
import kr.hs.dgsw.springproject.domain.question.repository.QuestionRepository;
import kr.hs.dgsw.springproject.domain.user.domain.User;
import kr.hs.dgsw.springproject.domain.user.service.UserService;
import kr.hs.dgsw.springproject.global.exception.ApplicationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("답변 서비스")
class AnswerServiceImplTest {

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private AnswerServiceImpl answerService;

    @Test
    @DisplayName("질문과 작성자가 존재하면 답변을 등록한다")
    void create_whenQuestionAndUserExist_returnsAnswerResponse() {
        User user = user(2L);
        Question question = question(1L, user);
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(userService.findById(2L)).thenReturn(user);
        when(answerRepository.save(any(Answer.class))).thenAnswer(invocation -> {
            Answer answer = invocation.getArgument(0);
            ReflectionTestUtils.setField(answer, "id", 3L);
            return answer;
        });

        AnswerResponse response = answerService.create(1L, new CreateAnswerRequest("답변 내용", 2L));

        assertEquals(3L, response.id());
        assertEquals("답변 내용", response.content());
        assertEquals(1L, response.questionId());
        assertEquals(2L, response.userId());
        verify(answerRepository).save(any(Answer.class));
    }

    @Test
    @DisplayName("존재하지 않는 질문의 답변 목록을 조회하면 예외가 발생한다")
    void getAllByQuestionId_whenQuestionDoesNotExist_throwsException() {
        when(questionRepository.findById(1L)).thenReturn(Optional.empty());

        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> answerService.getAllByQuestionId(1L)
        );

        assertEquals(AnswerStatusCode.QUESTION_NOT_FOUND, exception.getStatusCode());
    }

    @Test
    @DisplayName("존재하지 않는 답변을 수정하면 예외가 발생한다")
    void update_whenAnswerDoesNotExist_throwsException() {
        when(answerRepository.findById(1L)).thenReturn(Optional.empty());

        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> answerService.update(1L, new UpdateAnswerRequest("수정 내용"))
        );

        assertEquals(AnswerStatusCode.ANSWER_NOT_FOUND, exception.getStatusCode());
    }

    @Test
    @DisplayName("같은 질문에 다른 채택 답변이 있으면 채택할 수 없다")
    void accept_whenAnotherAnswerIsAlreadyAccepted_throwsException() {
        User user = user(2L);
        Question question = question(1L, user);
        Answer answer = answer(3L, "새 답변", question, user);
        Answer acceptedAnswer = answer(4L, "기존 채택 답변", question, user);
        acceptedAnswer.accept();
        when(answerRepository.findById(3L)).thenReturn(Optional.of(answer));
        when(answerRepository.findByQuestionIdAndAcceptedTrue(eq(1L))).thenReturn(Optional.of(acceptedAnswer));

        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> answerService.accept(3L)
        );

        assertEquals(AnswerStatusCode.ANSWER_ALREADY_ACCEPTED, exception.getStatusCode());
    }

    private User user(Long id) {
        User user = User.builder().name("답변 작성자").email("answer@example.com").password("password").build();
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private Question question(Long id, User user) {
        Question question = new Question("질문", "질문 내용", user);
        ReflectionTestUtils.setField(question, "id", id);
        return question;
    }

    private Answer answer(Long id, String content, Question question, User user) {
        Answer answer = new Answer(content, question, user);
        ReflectionTestUtils.setField(answer, "id", id);
        return answer;
    }
}
