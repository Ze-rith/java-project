package kr.hs.dgsw.springproject.domain.question.service;

import kr.hs.dgsw.springproject.domain.question.domain.Question;
import kr.hs.dgsw.springproject.domain.question.dto.request.CreateQuestionRequest;
import kr.hs.dgsw.springproject.domain.question.dto.request.UpdateQuestionRequest;
import kr.hs.dgsw.springproject.domain.question.exception.QuestionStatusCode;
import kr.hs.dgsw.springproject.domain.question.repository.QuestionRepository;
import kr.hs.dgsw.springproject.domain.user.domain.User;
import kr.hs.dgsw.springproject.domain.user.exception.UserStatusCode;
import kr.hs.dgsw.springproject.domain.user.repository.UserRepository;
import kr.hs.dgsw.springproject.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Question findById(Long id) {
        return getQuestion(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Question> findAll(String title) {
        if (title == null || title.isBlank()) {
            return questionRepository.findAllByOrderByIdDesc();
        }
        return questionRepository.findAllByTitleContainingOrderByIdDesc(title.strip());
    }

    @Override
    public Question create(CreateQuestionRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ApplicationException(UserStatusCode.USER_NOT_FOUND));

        return questionRepository.save(
                new Question(request.title(), request.content(), user)
        );
    }

    @Override
    public Question update(Long id, UpdateQuestionRequest request) {
        Question question = getQuestion(id);
        question.update(request.title(), request.content());

        return question;
    }

    @Override
    public void delete(Long id) {
        Question question = getQuestion(id);
        questionRepository.delete(question);
    }

    private Question getQuestion(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(QuestionStatusCode.QUESTION_NOT_FOUND));
    }
}
