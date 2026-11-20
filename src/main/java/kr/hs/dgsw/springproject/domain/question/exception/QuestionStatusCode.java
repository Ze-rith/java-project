package kr.hs.dgsw.springproject.domain.question.exception;

import kr.hs.dgsw.springproject.global.exception.statuscode.StatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum QuestionStatusCode implements StatusCode {
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "질문을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
