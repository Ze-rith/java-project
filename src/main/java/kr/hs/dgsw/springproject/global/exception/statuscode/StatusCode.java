package kr.hs.dgsw.springproject.global.exception.statuscode;

import kr.hs.dgsw.springproject.global.common.ApiResponse;
import kr.hs.dgsw.springproject.global.common.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface StatusCode {
    HttpStatus getHttpStatus();
    String getMessage();

    default ResponseEntity<ApiResponse<Void>> toEntity() {
        ErrorResponse error = ErrorResponse.of(getMessage());
        return ResponseEntity
                .status(getHttpStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(ApiResponse.error(getHttpStatus(), error));
    }

    default ResponseEntity<ApiResponse<Void>> toEntity(String message) {
        ErrorResponse error = ErrorResponse.of(message);
        return ResponseEntity
                .status(getHttpStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(ApiResponse.error(getHttpStatus(), error));
    }

    default ResponseEntity<ApiResponse<Void>> toEntity(String message, Map<String, String> details) {
        ErrorResponse error = ErrorResponse.of(message, details);
        return ResponseEntity
                .status(getHttpStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(ApiResponse.error(getHttpStatus(), error));
    }
}