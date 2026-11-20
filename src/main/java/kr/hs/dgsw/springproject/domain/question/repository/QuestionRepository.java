package kr.hs.dgsw.springproject.domain.question.repository;

import kr.hs.dgsw.springproject.domain.question.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
