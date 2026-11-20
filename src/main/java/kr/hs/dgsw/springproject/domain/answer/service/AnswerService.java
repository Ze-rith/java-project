package kr.hs.dgsw.springproject.domain.answer.service;

import kr.hs.dgsw.springproject.domain.answer.dto.request.CreateAnswerRequest;
import kr.hs.dgsw.springproject.domain.answer.dto.request.UpdateAnswerRequest;
import kr.hs.dgsw.springproject.domain.answer.dto.response.AnswerResponse;

import java.util.List;

public interface AnswerService {

    AnswerResponse create(Long questionId, CreateAnswerRequest request);

    List<AnswerResponse> getAllByQuestionId(Long questionId);

    AnswerResponse update(Long answerId, UpdateAnswerRequest request);

    void delete(Long answerId);

    AnswerResponse accept(Long answerId);
}
