package kr.hs.dgsw.springproject.answer.Repository;

import kr.hs.dgsw.springproject.answer.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
