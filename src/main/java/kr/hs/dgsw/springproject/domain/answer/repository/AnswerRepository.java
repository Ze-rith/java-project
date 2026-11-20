package kr.hs.dgsw.springproject.domain.answer.repository;

import kr.hs.dgsw.springproject.domain.answer.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    void deleteAllByUser_Id(Long userId);
    void deleteAllByQuestion_User_Id(Long userId);
    List<Answer> findAllByQuestionIdOrderByIdAsc(Long questionId);
    Optional<Answer> findByQuestionIdAndAcceptedTrue(Long questionId);
}
