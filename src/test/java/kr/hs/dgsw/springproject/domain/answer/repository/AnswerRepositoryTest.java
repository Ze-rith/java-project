package kr.hs.dgsw.springproject.domain.answer.repository;

import kr.hs.dgsw.springproject.domain.answer.domain.Answer;
import kr.hs.dgsw.springproject.domain.question.domain.Question;
import kr.hs.dgsw.springproject.domain.question.repository.QuestionRepository;
import kr.hs.dgsw.springproject.domain.user.domain.User;
import kr.hs.dgsw.springproject.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DisplayName("답변 리포지토리")
class AnswerRepositoryTest {

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("질문 ID로 답변을 등록 순서대로 조회한다")
    void findAllByQuestionIdOrderByIdAsc_returnsAnswersInCreatedOrder() {
        User user = userRepository.save(User.builder()
                .name("질문 작성자")
                .email("question@example.com")
                .password("password")
                .build());
        Question question = questionRepository.save(new Question("질문", "질문 내용", user));
        Answer first = answerRepository.save(new Answer("첫 번째 답변", question, user));
        Answer second = answerRepository.save(new Answer("두 번째 답변", question, user));

        List<Answer> answers = answerRepository.findAllByQuestionIdOrderByIdAsc(question.getId());

        assertEquals(List.of(first.getId(), second.getId()), answers.stream().map(Answer::getId).toList());
    }
}
