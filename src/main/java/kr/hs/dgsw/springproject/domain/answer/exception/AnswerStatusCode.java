package kr.hs.dgsw.springproject.domain.answer.exception;

import kr.hs.dgsw.springproject.global.exception.statuscode.StatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AnswerStatusCode implements StatusCode {
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "질문을 찾을 수 없습니다."),
    ANSWER_NOT_FOUND(HttpStatus.NOT_FOUND, "답변을 찾을 수 없습니다."),
    ANSWER_ALREADY_ACCEPTED(HttpStatus.CONFLICT, "이미 채택된 답변이 있습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
