package kr.hs.dgsw.springproject.domain.question.service;

import jakarta.persistence.EntityManager;
import kr.hs.dgsw.springproject.domain.answer.domain.Answer;
import kr.hs.dgsw.springproject.domain.question.domain.Question;
import kr.hs.dgsw.springproject.domain.question.dto.request.CreateQuestionRequest;
import kr.hs.dgsw.springproject.domain.question.dto.request.UpdateQuestionRequest;
import kr.hs.dgsw.springproject.domain.question.exception.QuestionStatusCode;
import kr.hs.dgsw.springproject.domain.question.repository.QuestionRepository;
import kr.hs.dgsw.springproject.domain.user.domain.User;
import kr.hs.dgsw.springproject.domain.user.exception.UserStatusCode;
import kr.hs.dgsw.springproject.domain.user.repository.UserRepository;
import kr.hs.dgsw.springproject.global.exception.ApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@DisplayName("질문 서비스 통합 테스트")
class QuestionServiceIntegrationTest {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    private QuestionServiceImpl questionService;
    private User user;

    @BeforeEach
    void setUp() {
        questionService = new QuestionServiceImpl(questionRepository, userRepository);
        user = userRepository.saveAndFlush(User.builder()
                .name("테스트 사용자")
                .email("user@example.com")
                .password("password")
                .build());
    }

    private Question saveQuestion(String title, String content) {
        return questionRepository.saveAndFlush(new Question(title, content, user));
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    @Nested
    @DisplayName("질문 등록")
    class Create {

        @Test
        @DisplayName("질문 등록 시 작성자와 함께 DB에 저장된다")
        void create_savesQuestionWithUser() {
            Question saved = questionService.create(
                    new CreateQuestionRequest(user.getId(), "질문 제목", "질문 내용")
            );
            flushAndClear();

            Question found = questionRepository.findById(saved.getId()).orElseThrow();
            assertThat(found.getTitle()).isEqualTo("질문 제목");
            assertThat(found.getContent()).isEqualTo("질문 내용");
            assertThat(found.getUser().getId()).isEqualTo(user.getId());
        }

        @Test
        @DisplayName("존재하지 않는 회원으로 질문 등록 시 USER_NOT_FOUND 예외가 발생하고 저장되지 않는다")
        void create_whenUserDoesNotExist_throwsUserNotFound() {
            assertThatThrownBy(() -> questionService.create(
                    new CreateQuestionRequest(-1L, "질문 제목", "질문 내용")
            ))
                    .isInstanceOf(ApplicationException.class)
                    .extracting(exception -> ((ApplicationException) exception).getStatusCode())
                    .isEqualTo(UserStatusCode.USER_NOT_FOUND);

            assertThat(questionRepository.count()).isZero();
        }
    }

    @Nested
    @DisplayName("질문 상세 조회")
    class FindById {

        @Test
        @DisplayName("저장된 질문을 ID로 조회한다")
        void findById_returnsSavedQuestion() {
            Question saved = saveQuestion("질문 제목", "질문 내용");

            Question found = questionService.findById(saved.getId());

            assertThat(found.getId()).isEqualTo(saved.getId());
            assertThat(found.getTitle()).isEqualTo("질문 제목");
            assertThat(found.getContent()).isEqualTo("질문 내용");
        }

        @Test
        @DisplayName("존재하지 않는 질문 조회 시 QUESTION_NOT_FOUND 예외가 발생한다")
        void findById_whenQuestionDoesNotExist_throwsQuestionNotFound() {
            assertThatThrownBy(() -> questionService.findById(-1L))
                    .isInstanceOf(ApplicationException.class)
                    .extracting(exception -> ((ApplicationException) exception).getStatusCode())
                    .isEqualTo(QuestionStatusCode.QUESTION_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("질문 목록 및 제목 검색")
    class FindAll {

        @Test
        @DisplayName("검색어 없이 조회하면 모든 질문을 최신순으로 반환한다")
        void findAll_withoutTitle_returnsAllQuestionsNewestFirst() {
            Question first = saveQuestion("스프링 질문", "내용1");
            Question second = saveQuestion("자바 질문", "내용2");
            Question third = saveQuestion("JPA 질문", "내용3");

            List<Question> questions = questionService.findAll(null);

            assertThat(questions).extracting(Question::getId)
                    .containsExactly(third.getId(), second.getId(), first.getId());
        }

        @Test
        @DisplayName("공백 검색어로 조회하면 모든 질문을 반환한다")
        void findAll_withBlankTitle_returnsAllQuestions() {
            saveQuestion("스프링 질문", "내용1");
            saveQuestion("자바 질문", "내용2");

            List<Question> questions = questionService.findAll("   ");

            assertThat(questions).hasSize(2);
        }

        @Test
        @DisplayName("제목에 검색어가 포함된 질문만 최신순으로 반환한다")
        void findAll_withTitle_returnsMatchingQuestionsNewestFirst() {
            Question first = saveQuestion("스프링 부트 질문", "내용1");
            saveQuestion("자바 질문", "내용2");
            Question third = saveQuestion("스프링 시큐리티 질문", "내용3");

            List<Question> questions = questionService.findAll("스프링");

            assertThat(questions).extracting(Question::getId)
                    .containsExactly(third.getId(), first.getId());
        }

        @Test
        @DisplayName("검색어 앞뒤 공백은 제거하고 검색한다")
        void findAll_withPaddedTitle_stripsWhitespace() {
            Question saved = saveQuestion("스프링 질문", "내용");
            saveQuestion("자바 질문", "내용");

            List<Question> questions = questionService.findAll("  스프링  ");

            assertThat(questions).extracting(Question::getId).containsExactly(saved.getId());
        }

        @Test
        @DisplayName("검색어와 일치하는 질문이 없으면 빈 목록을 반환한다")
        void findAll_whenNoTitleMatches_returnsEmptyList() {
            saveQuestion("스프링 질문", "내용");

            List<Question> questions = questionService.findAll("파이썬");

            assertThat(questions).isEmpty();
        }

        @Test
        @DisplayName("질문이 하나도 없으면 빈 목록을 반환한다")
        void findAll_whenNoQuestions_returnsEmptyList() {
            assertThat(questionService.findAll(null)).isEmpty();
        }
    }

    @Nested
    @DisplayName("질문 수정")
    class Update {

        @Test
        @DisplayName("질문을 수정하면 변경 내용이 DB에 반영된다")
        void update_persistsChangedTitleAndContent() {
            Question saved = saveQuestion("기존 제목", "기존 내용");

            questionService.update(saved.getId(), new UpdateQuestionRequest("변경된 제목", "변경된 내용"));
            flushAndClear();

            Question updated = questionRepository.findById(saved.getId()).orElseThrow();
            assertThat(updated.getTitle()).isEqualTo("변경된 제목");
            assertThat(updated.getContent()).isEqualTo("변경된 내용");
        }

        @Test
        @DisplayName("존재하지 않는 질문 수정 시 QUESTION_NOT_FOUND 예외가 발생한다")
        void update_whenQuestionDoesNotExist_throwsQuestionNotFound() {
            assertThatThrownBy(() -> questionService.update(
                    -1L, new UpdateQuestionRequest("변경된 제목", "변경된 내용")
            ))
                    .isInstanceOf(ApplicationException.class)
                    .extracting(exception -> ((ApplicationException) exception).getStatusCode())
                    .isEqualTo(QuestionStatusCode.QUESTION_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("질문 삭제")
    class Delete {

        @Test
        @DisplayName("질문 삭제 후 저장소에서 조회되지 않는다")
        void delete_removesQuestionFromRepository() {
            Question saved = saveQuestion("질문 제목", "질문 내용");

            questionService.delete(saved.getId());
            flushAndClear();

            assertThat(questionRepository.findById(saved.getId())).isEmpty();
        }

        @Test
        @DisplayName("질문 삭제 시 달린 답변도 함께 삭제된다")
        void delete_removesAnswersOfQuestion() {
            Question saved = saveQuestion("질문 제목", "질문 내용");
            Answer answer = new Answer("답변 내용", saved, user);
            entityManager.persist(answer);
            flushAndClear();

            questionService.delete(saved.getId());
            flushAndClear();

            assertThat(entityManager.find(Answer.class, answer.getId())).isNull();
        }

        @Test
        @DisplayName("존재하지 않는 질문 삭제 시 QUESTION_NOT_FOUND 예외가 발생한다")
        void delete_whenQuestionDoesNotExist_throwsQuestionNotFound() {
            assertThatThrownBy(() -> questionService.delete(-1L))
                    .isInstanceOf(ApplicationException.class)
                    .extracting(exception -> ((ApplicationException) exception).getStatusCode())
                    .isEqualTo(QuestionStatusCode.QUESTION_NOT_FOUND);
        }
    }
}
