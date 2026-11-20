package kr.hs.dgsw.springproject.domain.user.service;

import jakarta.persistence.EntityManager;
import kr.hs.dgsw.springproject.domain.auth.service.EmailService;
import kr.hs.dgsw.springproject.domain.answer.domain.Answer;
import kr.hs.dgsw.springproject.domain.answer.repository.AnswerRepository;
import kr.hs.dgsw.springproject.domain.question.domain.Question;
import kr.hs.dgsw.springproject.domain.question.repository.QuestionRepository;
import kr.hs.dgsw.springproject.domain.user.domain.User;
import kr.hs.dgsw.springproject.domain.user.dto.request.CreateUserRequest;
import kr.hs.dgsw.springproject.domain.user.dto.request.UpdateUserRequest;
import kr.hs.dgsw.springproject.domain.user.exception.UserStatusCode;
import kr.hs.dgsw.springproject.domain.user.repository.UserRepository;
import kr.hs.dgsw.springproject.global.exception.ApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@DataJpaTest
@DisplayName("회원 서비스 통합 테스트")
class UserServiceIntegrationTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private EntityManager entityManager;

    private EmailService emailService;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        emailService = mock(EmailService.class);
        userService = new UserServiceImpl(userRepository, emailService, answerRepository, questionRepository);
    }

    @Test
    @DisplayName("회원 생성 시 DB에 저장하고 이메일 인증을 소비한다")
    void create_savesUserAndConsumesEmailVerification() {
        User saved = userService.create(new CreateUserRequest(
                "테스트 사용자", "user@example.com", "password"
        ));

        entityManager.flush();
        entityManager.clear();

        Optional<User> found = userRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.orElseThrow().getName()).isEqualTo("테스트 사용자");
        assertThat(found.orElseThrow().getEmail()).isEqualTo("user@example.com");
        verify(emailService).consumeVerifiedEmail("user@example.com");
    }

    @Test
    @DisplayName("저장된 회원을 ID로 조회한다")
    void findById_returnsSavedUser() {
        User saved = userRepository.saveAndFlush(User.builder()
                .name("테스트 사용자")
                .email("user@example.com")
                .password("password")
                .build());

        User found = userService.findById(saved.getId());

        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getName()).isEqualTo("테스트 사용자");
        assertThat(found.getEmail()).isEqualTo("user@example.com");
    }

    @Test
    @DisplayName("존재하지 않는 회원 조회 시 USER_NOT_FOUND 예외가 발생한다")
    void findById_whenUserDoesNotExist_throwsUserNotFound() {
        assertThatThrownBy(() -> userService.findById(-1L))
                .isInstanceOf(ApplicationException.class)
                .extracting(exception -> ((ApplicationException) exception).getStatusCode())
                .isEqualTo(UserStatusCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("이미 등록된 이메일로 회원 생성 시 중복 이메일 예외가 발생한다")
    void create_whenEmailAlreadyExists_throwsDuplicateEmail() {
        userRepository.saveAndFlush(User.builder()
                .name("기존 사용자")
                .email("user@example.com")
                .password("password")
                .build());

        assertThatThrownBy(() -> userService.create(new CreateUserRequest(
                "새 사용자", "user@example.com", "password"
        )))
                .isInstanceOf(ApplicationException.class)
                .extracting(exception -> ((ApplicationException) exception).getStatusCode())
                .isEqualTo(UserStatusCode.EMAIL_ALREADY_EXISTS);

        verifyNoInteractions(emailService);
    }

    @Test
    @DisplayName("회원 정보를 수정하면 변경 내용이 DB에 반영된다")
    void update_persistsChangedNameAndEmail() {
        User saved = userRepository.saveAndFlush(User.builder()
                .name("기존 사용자")
                .email("old@example.com")
                .password("password")
                .build());

        userService.update(saved.getId(), new UpdateUserRequest("변경된 사용자", "new@example.com"));
        entityManager.flush();
        entityManager.clear();

        User updated = userRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("변경된 사용자");
        assertThat(updated.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    @DisplayName("회원 삭제 후 저장소에서 조회되지 않는다")
    void delete_removesUserFromRepository() {
        User saved = userRepository.saveAndFlush(User.builder()
                .name("테스트 사용자")
                .email("user@example.com")
                .password("password")
                .build());

        assertThat(userService.delete(saved.getId())).isTrue();
        entityManager.flush();
        entityManager.clear();

        assertThat(userRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    @DisplayName("회원 삭제 시 작성한 질문과 답변도 삭제한다")
    void delete_removesUserQuestionsAndAnswers() {
        User userToDelete = userRepository.saveAndFlush(User.builder()
                .name("삭제할 사용자")
                .email("delete@example.com")
                .password("password")
                .build());
        User otherUser = userRepository.saveAndFlush(User.builder()
                .name("다른 사용자")
                .email("other@example.com")
                .password("password")
                .build());

        Question ownedQuestion = questionRepository.saveAndFlush(
                new Question("내 질문", "질문 내용", userToDelete));
        Question otherQuestion = questionRepository.saveAndFlush(
                new Question("다른 질문", "질문 내용", otherUser));
        answerRepository.saveAndFlush(new Answer("내 답변", otherQuestion, userToDelete));
        answerRepository.saveAndFlush(new Answer("다른 사람의 답변", ownedQuestion, otherUser));
        Long userId = userToDelete.getId();
        Long otherUserId = otherUser.getId();

        assertThat(userService.delete(userId)).isTrue();
        entityManager.flush();
        entityManager.clear();

        assertThat(userRepository.findById(userId)).isEmpty();
        assertThat(questionRepository.findAll())
                .extracting(Question::getUser)
                .extracting(User::getId)
                .containsExactly(otherUserId);
        assertThat(answerRepository.findAll())
                .extracting(Answer::getUser)
                .extracting(User::getId)
                .isEmpty();
    }
}
