package kr.hs.dgsw.springproject.domain.answer.service;

import kr.hs.dgsw.springproject.domain.answer.domain.Answer;
import kr.hs.dgsw.springproject.domain.answer.dto.request.CreateAnswerRequest;
import kr.hs.dgsw.springproject.domain.answer.dto.request.UpdateAnswerRequest;
import kr.hs.dgsw.springproject.domain.answer.dto.response.AnswerResponse;
import kr.hs.dgsw.springproject.domain.answer.exception.AnswerStatusCode;
import kr.hs.dgsw.springproject.domain.answer.repository.AnswerRepository;
import kr.hs.dgsw.springproject.domain.question.domain.Question;
import kr.hs.dgsw.springproject.domain.question.repository.QuestionRepository;
import kr.hs.dgsw.springproject.domain.user.domain.User;
import kr.hs.dgsw.springproject.domain.user.service.UserService;
import kr.hs.dgsw.springproject.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserService userService;

    @Override
    @Transactional
    public AnswerResponse create(Long questionId, CreateAnswerRequest request) {
        Question question = getQuestion(questionId);
        User user = userService.findById(request.userId());

        Answer answer = answerRepository.save(new Answer(request.content(), question, user));
        return AnswerResponse.of(answer);
    }

    @Override
    public List<AnswerResponse> getAllByQuestionId(Long questionId) {
        getQuestion(questionId);
        return answerRepository.findAllByQuestionIdOrderByIdAsc(questionId).stream()
                .map(AnswerResponse::of)
                .toList();
    }

    @Override
    @Transactional
    public AnswerResponse update(Long answerId, UpdateAnswerRequest request) {
        Answer answer = getAnswer(answerId);
        answer.update(request.content());
        return AnswerResponse.of(answer);
    }

    @Override
    @Transactional
    public void delete(Long answerId) {
        answerRepository.delete(getAnswer(answerId));
    }

    @Override
    @Transactional
    public AnswerResponse accept(Long answerId) {
        Answer answer = getAnswer(answerId);
        answerRepository.findByQuestionIdAndAcceptedTrue(answer.getQuestion().getId())
                .filter(acceptedAnswer -> !acceptedAnswer.getId().equals(answerId))
                .ifPresent(acceptedAnswer -> {
                    throw ApplicationException.of(AnswerStatusCode.ANSWER_ALREADY_ACCEPTED);
                });

        answer.accept();
        return AnswerResponse.of(answer);
    }

    private Question getQuestion(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> ApplicationException.of(AnswerStatusCode.QUESTION_NOT_FOUND));
    }

    private Answer getAnswer(Long answerId) {
        return answerRepository.findById(answerId)
                .orElseThrow(() -> ApplicationException.of(AnswerStatusCode.ANSWER_NOT_FOUND));
    }
}
