package kr.hs.dgsw.springproject.domain.question.controller;

import jakarta.validation.Valid;
import kr.hs.dgsw.springproject.domain.question.domain.Question;
import kr.hs.dgsw.springproject.domain.question.dto.request.CreateQuestionRequest;
import kr.hs.dgsw.springproject.domain.question.dto.request.UpdateQuestionRequest;
import kr.hs.dgsw.springproject.domain.question.dto.response.CreateQuestionResponse;
import kr.hs.dgsw.springproject.domain.question.dto.response.QuestionFindAllResponse;
import kr.hs.dgsw.springproject.domain.question.dto.response.QuestionFindByIdResponse;
import kr.hs.dgsw.springproject.domain.question.dto.response.UpdateQuestionResponse;
import kr.hs.dgsw.springproject.domain.question.service.QuestionService;
import kr.hs.dgsw.springproject.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CreateQuestionResponse> createQuestion(@RequestBody @Valid CreateQuestionRequest request) {
        Question question = questionService.create(request);

        return ApiResponse.created(CreateQuestionResponse.of(question), "질문이 등록되었습니다.");
    }

    @GetMapping
    public ApiResponse<List<QuestionFindAllResponse>> getQuestions(@RequestParam(value = "title", required = false) String title) {
        List<Question> questions = questionService.findAll(title);

        return ApiResponse.ok(QuestionFindAllResponse.of(questions), "질문 목록을 조회했습니다.");
    }

    @GetMapping("/{id}")
    public ApiResponse<QuestionFindByIdResponse> getQuestion(@PathVariable("id") Long id) {
        Question question = questionService.findById(id);

        return ApiResponse.ok(QuestionFindByIdResponse.of(question), "질문을 조회했습니다.");
    }

    @PutMapping("/{id}")
    public ApiResponse<UpdateQuestionResponse> updateQuestion(@PathVariable("id") Long id, @RequestBody @Valid UpdateQuestionRequest request) {
        Question question = questionService.update(id, request);

        return ApiResponse.ok(UpdateQuestionResponse.of(question), "질문을 수정했습니다.");
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQuestion(@PathVariable("id") Long id) {
        questionService.delete(id);
    }
}
