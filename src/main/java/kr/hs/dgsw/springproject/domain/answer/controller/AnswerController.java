package kr.hs.dgsw.springproject.domain.answer.controller;

import jakarta.validation.Valid;
import kr.hs.dgsw.springproject.domain.answer.dto.request.CreateAnswerRequest;
import kr.hs.dgsw.springproject.domain.answer.dto.request.UpdateAnswerRequest;
import kr.hs.dgsw.springproject.domain.answer.dto.response.AnswerResponse;
import kr.hs.dgsw.springproject.domain.answer.service.AnswerService;
import kr.hs.dgsw.springproject.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping("/questions/{questionId}/answers")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AnswerResponse> createAnswer(
            @PathVariable Long questionId,
            @Valid @RequestBody CreateAnswerRequest request
    ) {
        return ApiResponse.created(answerService.create(questionId, request), "답변이 등록되었습니다.");
    }

    @GetMapping("/questions/{questionId}/answers")
    public ApiResponse<List<AnswerResponse>> getAnswers(@PathVariable Long questionId) {
        return ApiResponse.ok(answerService.getAllByQuestionId(questionId), "답변 목록을 조회했습니다.");
    }

    @PutMapping("/answers/{answerId}")
    public ApiResponse<AnswerResponse> updateAnswer(
            @PathVariable Long answerId,
            @Valid @RequestBody UpdateAnswerRequest request
    ) {
        return ApiResponse.ok(answerService.update(answerId, request), "답변이 수정되었습니다.");
    }

    @DeleteMapping("/answers/{answerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAnswer(@PathVariable Long answerId) {
        answerService.delete(answerId);
    }

    @PutMapping("/answers/{answerId}/accept")
    public ApiResponse<AnswerResponse> acceptAnswer(@PathVariable Long answerId) {
        return ApiResponse.ok(answerService.accept(answerId), "답변이 채택되었습니다.");
    }
}
