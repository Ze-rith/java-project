package kr.hs.dgsw.springproject.domain.question.service;

import kr.hs.dgsw.springproject.domain.question.domain.Question;
import kr.hs.dgsw.springproject.domain.question.dto.request.CreateQuestionRequest;
import kr.hs.dgsw.springproject.domain.question.dto.request.UpdateQuestionRequest;

import java.util.List;

public interface QuestionService {
    Question findById(Long id);
    List<Question> findAll(String title);
    Question create(CreateQuestionRequest request);
    Question update(Long id, UpdateQuestionRequest request);
    void delete(Long id);
}
