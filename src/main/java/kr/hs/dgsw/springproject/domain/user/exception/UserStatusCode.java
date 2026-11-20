package kr.hs.dgsw.springproject.domain.user.exception;

import kr.hs.dgsw.springproject.global.exception.statuscode.StatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserStatusCode implements StatusCode {
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다.")
    ;


    private final HttpStatus httpStatus;
    private final String message;
}
