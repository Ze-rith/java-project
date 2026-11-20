package kr.hs.dgsw.springproject.domain.question.repository;

import kr.hs.dgsw.springproject.domain.question.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    void deleteAllByUser_Id(Long userId);
    List<Question> findAllByOrderByIdDesc();
    List<Question> findAllByTitleContainingOrderByIdDesc(String title);
}
