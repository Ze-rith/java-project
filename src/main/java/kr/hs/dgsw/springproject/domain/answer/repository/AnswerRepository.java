package kr.hs.dgsw.springproject.domain.answer.repository;

import kr.hs.dgsw.springproject.domain.answer.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    void deleteAllByUser_Id(Long userId);

    void deleteAllByQuestion_User_Id(Long userId);
}
